package com.geekbrains;

import com.geekbrains.model.FileMessage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class test {

    public static void main(String[] args) {

        Path baseDir = Paths.get(System.getProperty("user.home"));

        Path baseDir2 = Paths.get(System.getProperty("user.dir"));
        Path dir3 = baseDir2.resolve("storage");
        Path dir4 = dir3.resolve("ServerCommonStorage");
        Path dir5 = dir4.getParent();

        System.out.println(baseDir);
        System.out.println(baseDir2);

        System.out.println(baseDir2.getParent());

        System.out.println(dir4);
        System.out.println("parent: " + dir5.getParent().getParent());


        System.out.println("check GIT commit from IDEA");


        String s = "Weclome Pidor";

        System.out.println(s.split(" ")[1]);




    }

}
