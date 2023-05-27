package com.yooondooo.WORDLY;


import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Handler {
    public boolean checki = false;
    private boolean isChecki = true;
    private int height;
    private int lenght;
    private final int countLetters;
    public String word;
    private final PlayScene context;
    private String clueWord;
    private String[] wordly = new String[5];
    private String alf = "абвгдежзийклмнопрстуфхцчшщъыьэюя";
    private final TextView[][] textView;
    private final Resources resources;
    private final List<String> file = new ArrayList<>();
    private final int dictSize = 6963;
    private  String noDelete = "";
    private final List<String> copyWords = new ArrayList<>();
    private final String[] yellowLetters = new String[5];



    public Handler(TextView[][] textView, PlayScene context) {
        this.textView = textView;
        clueWord = ".....";
        height = 0;
        lenght = 0;
        AssetManager assetManager = context.getAssets();
        this.context = context;
        this.resources = new Resources(assetManager, new DisplayMetrics(), new Configuration());
        Random random = new Random();
        countLetters = random.nextInt(dictSize);
        word = ret();
        for (int i = 0; i < 5; i++){
            yellowLetters[i] = "";
        }
    }

    public void addLetter(String letter) {
        if (lenght < 5 && height < 6 && !checki) {
            textView[height][lenght].setText(letter);
            wordly[lenght] = letter;
            lenght++;
        }
        if (lenght == 5) {
            lenght = 0;
            if (check()) {
                if (isChecki) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Вы победили")
                            .setCancelable(false)
                            .setPositiveButton("Хорошо", (dialog, which) -> dialog.cancel());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                checki = true;
            }
            wordly = new String[5];
            height++;
        }
        if (height == 6 && !checki) {
            if (isChecki) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Вы проиграли")
                        .setMessage("Загаданное слово: " + word)
                        .setCancelable(false)
                        .setPositiveButton("Попробую еще раз", (dialog, which) -> dialog.cancel());
                AlertDialog dialog = builder.create();
                dialog.show();
                checki = true;
                isChecki = false;
            }
        }
    }

    public void delete() {
        if (lenght > 0) {
            lenght--;
            textView[height][lenght].setText("");
        }
    }
    public String clue(){
        String clueW ="нет слова";
        String[] letters = noDelete.split("");
        boolean checkClue = true;
        for (String line: copyWords){
                for (String letter : letters) {
                    if (!line.contains(letter)) {
                        checkClue = false;
                        break;
                    }
                }
                String[] let = line.split("");
                for (int i = 0; i < 5; i++) {
                    System.out.println(wordly[i]);
                    if (!alf.contains(let[i]) || yellowLetters[i].contains(let[i])) {
                        checkClue = false;
                        break;
                    }
                }
            if (line.matches(clueWord) && checkClue){
                clueW = line;
                break;
            }
            checkClue = true;
        }
        return clueW;
    }

    public String ret() {
        InputStream inFile;
        String wordQ = "";
        inFile = resources.openRawResource(R.raw.dict_7000);
        BufferedReader br = new BufferedReader(new InputStreamReader(inFile));
        for (int i = 0; i < dictSize; i++) {
            try {
                file.add(br.readLine());
                copyWords.add(file.get(i));
                if (i == countLetters) {
                    wordQ = file.get(i);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return wordQ;
    }

    public boolean check() {
        String[] wordForCheck = word.split("");
        char[] chars = clueWord.toCharArray();
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (wordForCheck[i].equals(wordly[i].toLowerCase())) {
                textView[height][i].setBackgroundResource(R.color.green);
                wordForCheck[i] = "*";
                chars[i] = wordly[i].toLowerCase().charAt(0);
                if (!noDelete.contains(wordly[i])){
                    noDelete+=wordly[i].toLowerCase();
                }
                wordly[i] = "-";
                count++;

            }
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (wordly[i].toLowerCase().equals(wordForCheck[j])) {
                    textView[height][i].setBackgroundResource(R.color.yellow);
                    wordForCheck[j] = "*";
                    yellowLetters[i] += wordly[i].toLowerCase();
                    if (!noDelete.contains(wordly[i])){
                        noDelete+=wordly[i].toLowerCase();
                    }
                    break;
                }
                else{
                    wordly[i] = wordly[i].toLowerCase();
                }
            }
        }
        for (int i = 0; i < 5; i++){
            if (alf.contains(wordly[i]) && !noDelete.contains(wordly[i])){
                alf = alf.replace(wordly[i],"");
            }
            System.out.println(wordly[i]);
            System.out.println(wordForCheck[i]);
        }
        clueWord = String.valueOf(chars);
        return count == 5;
    }
}