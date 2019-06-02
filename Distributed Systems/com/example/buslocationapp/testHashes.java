package com.example.buslocationapp;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;


public class testHashes {
    public static void main(String[] args) {
        final int  MOD = 10; //modulo
        new Reader("busLinesNew.txt", "busPositionsNew.txt", "RouteCodesNew.txt");
        String[][] busLinesHash = new String[20][2];
        for (int i = 0; i < 20; i++) {
            busLinesHash[i][0] = Reader.getBus()[1];
            busLinesHash[i][1] = calculateHash(busLinesHash[i][0]);
        }

        Reader.getBrokerList("brokerIPs.txt");
        List<String> ips = Reader.getIPs();
        String[][] ipHashes = new String[3][3];
        for (int j = 0; j < 3; j++) {
            ipHashes[j][0] = calculateHash(ips.get(j) + "4321");
            ipHashes[j][1] = "";
            ipHashes[j][2] = ips.get(j);
        }
        for (int i = 0; i < 20; i++) { //applying mod operation to MD5 Hashes
            busLinesHash[i][1] =new BigInteger(busLinesHash[i][1]).mod(BigInteger.valueOf(MOD)).toString();
        }
        for (int i = 0; i < 3; i++) { //same
            ipHashes[i][0] = new BigInteger(ipHashes[i][0]).mod(BigInteger.valueOf(MOD)).toString();
        }

        Reader.sort2D(busLinesHash,1);
        for (int i = 0; i < 20; i++) {
            System.out.println(busLinesHash[i][0]);
        }

        Arrays.sort(ipHashes, (entry1, entry2) -> { //lambda expression for sorting 2d arrays
            final int hash1 = Integer.parseInt(entry1[0]);
            final int hash2 = Integer.parseInt(entry2[0]);
            return hash1-hash2;
        });
        Arrays.sort(busLinesHash, (entry1, entry2) -> { //same
            final int hash1 = Integer.parseInt(entry1[1]);
            final int hash2 = Integer.parseInt(entry2[1]);
            return hash1 -hash2;
        });
        for (int i = 0; i < 20; i++) {
            System.out.println("line " + busLinesHash[i][0] + ", hash=" + busLinesHash[i][1]);
        }
        System.out.println("ip hashes");
        for (int i = 0; i < 3 ; i++) {
            System.out.println(ipHashes[i][2] + ": " + ipHashes[i][0]);

        }
        int maxIndex; //last element we added. We don't wanna iterate over it again
        for (maxIndex = 0; maxIndex < 20; maxIndex++) { //adding elements to lowest broker
            if(Integer.parseInt(busLinesHash[maxIndex][1])<Integer.parseInt(ipHashes[0][0])){
                ipHashes[0][1]+=busLinesHash[maxIndex][0]+",";
            }else{
                break;
            }
        }
        for (int i = 1; i < 3; i++) {
            for (int j = maxIndex; j < 20; j++) {
                if(Integer.parseInt(busLinesHash[j][1])>=Integer.parseInt(ipHashes[i-1][0])&&
                        Integer.parseInt(busLinesHash[j][1])<Integer.parseInt(ipHashes[i][0])){
                    ipHashes[i][1]+=busLinesHash[j][0]+",";
                }else{
                    if(i==2){
                        ipHashes[0][1]+=busLinesHash[j][0]+",";
                    }else{
                        maxIndex=j;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < 3 ; i++) {
            System.out.println(ipHashes[i][2] + ": " + ipHashes[i][1]);

        }
    }

    public static String calculateHash(String message) {//hashing function
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5"); //using MD5 algorithm
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update((message).getBytes());
        byte[] md = md5.digest();
        BigInteger big = new BigInteger(1, md);
        String Hash = big.toString();
//        while (Hash.length() < 32) {
//            Hash += "0";
//        }
        return Hash;
    }
}
