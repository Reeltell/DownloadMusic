import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите URL Музыки:");
        String urlMusic = scanner.nextLine();

        System.out.println("Введите URL Картинки:");
        String urlPicture = scanner.nextLine();

        Thread mp3Thread = new Thread(() -> downloadWithUrl(urlMusic, "file.mp3"));
        Thread pictureThread = new Thread(() -> downloadWithUrl(urlPicture, "picture.jpg"));

        mp3Thread.start();
        pictureThread.start();
    }

    public static void downloadWithUrl(String url, String filePath) {
        try {
            URLConnection connection = new URL(url).openConnection();
            int totalSize = connection.getContentLength(); // Получаем общий размер файла
            InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = new FileOutputStream(filePath);

            byte[] buffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                // Выводим прогресс загрузки
                int progressPercentage = (int) ((double) totalBytesRead / totalSize * 100);
                System.out.println("Загрузка " + filePath + ": " + progressPercentage + "%");
            }

            outputStream.close();
            inputStream.close();

            // Открываем файл после завершения загрузки
            Desktop.getDesktop().open(new File(filePath));

            // Проверяем тип загруженного файла
            if (filePath.equals("picture.jpg")) {
                String pictureType = getFileType(filePath);
                if (Objects.equals(pictureType, "JPEG")) {
                    System.out.println("Тип файла совпадает: JPEG");
                } else {
                    System.out.println("Тип файла не совпадает: " + pictureType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileType(String filename) {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] magicBytes = new byte[4];
            if (fis.read(magicBytes) != -1) {
                return determineFileType(magicBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Неопределенный тип";
    }

    public static String determineFileType(byte[] magicBytes) {
        if (magicBytes.length < 4) {
            return "Неопределенный тип";
        }

        // Определяем типы файлов по их магическим числам
        if (magicBytes[0] == (byte) 0x49 && magicBytes[1] == (byte) 0x44 &&
                magicBytes[2] == (byte) 0x33) {
            return "MP3";
        } else if (magicBytes[0] == (byte) 0x89 && magicBytes[1] == (byte) 0x50 &&
                magicBytes[2] == (byte) 0x4E && magicBytes[3] == (byte) 0x47) {
            return "PNG";
        } else if (magicBytes[0] == (byte) 0xFF && magicBytes[1] == (byte) 0xD8) {
            return "JPEG";
        } else if (magicBytes[0] == (byte) 0x25 && magicBytes[1] == (byte) 0x50 &&
                magicBytes[2] == (byte) 0x44 && magicBytes[3] == (byte) 0x46) {
            return "PDF";
        }

        return "Неопределенный тип";
    }
}