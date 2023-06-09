package com.example.task.service;

import com.example.task.domain.Notification;
import com.example.task.domain.Root;
import com.example.task.domain.Totals;
import com.example.task.domain.Transaction;
import com.example.task.exceptions.InCorrectCurrencyException;
import com.example.task.exceptions.TransactionTypeNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String jarFilePath = "src/main/resources/files/trn2msg.jar";
    private static final String entryName = "transactionfile.txt";
    private static final String notificationFileName = "src/main/resources/files/notification.json";
    Map<String, String> transactionTypeMap = Map.of("00", "purchase", "01", "withdraw");
    Map<String, String> currencyMap = Map.of("840", "usd", "978", "eur", "826", "gbp", "643", "rub");


    public Notification printNotification(){

        List<Transaction> list = createListOfTransactions();
        Notification notification = createNotification(list);
        log.info("Notification has created");
        if(new File(notificationFileName).exists()){
            printNotificationToFile(notification);
            log.info("Notification has written to file");
            return new Notification();
        }
        log.info("Notification File is not found! ");
        return notification;
    }
    public Notification createNotification(List<Transaction> list){
        return Notification.builder()
                .root(
                        Root.builder()
                                .msgList(
                                        list.stream().map(this::getTransactionMessage).toList()
                                )
                                .totals(createTotals(list))
                                .build()
                )
                .build();
    }

    public List<Transaction> createListOfTransactions(){
        log.info("Start of file conversion");
        List<String> stringList = readLinesFromJarFile();
        log.info("End of file conversion");
        return stringList.stream()
                .map(unit -> (createTransactionFromString(unit)))
                .toList();
    }

    public String getTransactionMessage(Transaction transaction){
        return String.format("%s with card %s on %s, amount %s %s",
                transaction.getType(), transaction.getPan(), transaction.getTransactionTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh.mm.ss")), new DecimalFormat("#0.00").format(transaction.getAmount()), transaction.getCurrency());
    }

    public Totals createTotals(List<Transaction> transactions){
        return Totals.builder()
                .cnt(transactions.size())
                .sum(
                        transactions.stream()
                                .mapToDouble(transaction-> transaction.getAmount()).sum()
                )
                .date(LocalDateTime.now())
                .build();
    }

    public Transaction createTransactionFromString(String input){

        String transactionTypeCode = input.substring(0,2);
        String panCode = input.substring(2, 18);
        String amountCode = input.substring(18, 30);
        String timeCode = input.substring(30, 44);
        String currencyCode = input.substring(44, 47);
        validate(transactionTypeCode, currencyCode);

        return  Transaction.builder()
                    .type(transactionTypeMap.get(transactionTypeCode))
                    .pan(panCode.substring(0,5)+"******"+panCode.substring(11,15))
                    .transactionTime(LocalDateTime.parse(timeCode, DateTimeFormatter.ofPattern(("yyyyMMddHHmmss"))))
                    .amount(Double.parseDouble(amountCode)/100)
                    .currency(currencyMap.get(currencyCode))
                    .build();

    }

    public void validate( String transactionTypeCode, String currencyCode){

        if (!this.transactionTypeMap.containsKey(transactionTypeCode)) {
            log.info("transaction type {} is not found", transactionTypeCode);
            throw new TransactionTypeNotFoundException();
        }
        if (!this.currencyMap.containsKey(currencyCode)) {
            log.info("currency {} is not found ", currencyCode);
            throw new InCorrectCurrencyException();
        }
    }

    @SneakyThrows
    public void printNotificationToFile(Notification notification){
        objectMapper.findAndRegisterModules();
        objectMapper.writeValue(new File(notificationFileName), notification);
    }


    @SneakyThrows
    public static List<String> readLinesFromJarFile() {
        List<String> lines = new ArrayList<>();

        try (ZipFile jarFile = new ZipFile(jarFilePath)) {
            ZipEntry entry = jarFile.getEntry(entryName);

            if (entry != null) {
                try (InputStream inputStream = jarFile.getInputStream(entry);
                     InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            } else {
                log.error("Entry not found: " + entryName);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return lines;
    }

}
