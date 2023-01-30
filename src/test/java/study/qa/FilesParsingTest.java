package study.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import study.qa.model.Glossary;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selectors.withTagAndText;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class FilesParsingTest {
    ClassLoader cl = FilesParsingTest.class.getClassLoader();

    @Test
    @DisplayName("Проверка содержимого загружаемого pdf-файла")
    void pdfParseTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File downloadedPdf = $(withTagAndText("a", "PDF download")).download();
        PDF content = new PDF(downloadedPdf);
        assertThat(content.author).contains("Sam Brannen");
    }

    @Test
    @DisplayName("Проверка содержимого xlsx файла, взятого из classPath при помощи ClassLoader")
    void xlsClassPathParseTest() throws Exception {
        try (InputStream resourceAsStream = cl.getResourceAsStream("example/test.xlsx")) {
            XLS content = new XLS(resourceAsStream);
            assertThat(content.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue())
                    .contains("Шифр, наименование методики измерений");
        }
    }

    @Test
    @DisplayName("Проверка содержимого csv файла, взятого из classPath при помощи ClassLoader")
    void csvParseTest() throws Exception {
        try (
                InputStream resourceAsStream = cl.getResourceAsStream("example/qa_guru.csv");
                CSVReader reader = new CSVReader(new InputStreamReader(resourceAsStream))
        ) {
            List<String[]> content = reader.readAll(); // будет содержать количество элементов, равное количеству строк в csv-файле, а каждая строка будет содержать элементы равные стрингам, разделённым запятыми

            assertThat(content.get(0)[1]).contains("Lesson");
        }
    }

    @Test
    @DisplayName("Проверка содержимого zip файла, содержащего один файл, взятого из classPath при помощи ClassLoader")
    void zipParseTest() throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("example/example.zip");
                ZipInputStream zis = new ZipInputStream(resource)
        ) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                assertThat(entry.getName()).isEqualTo("cat_notebook.jpeg");
            }
        }
    }

    @Test
    @DisplayName("Проверка содержимого json файла, без применения pojo-класса, взятого из classPath при помощи ClassLoader")
    void jsonParseTest() throws Exception {
        Gson gson = new Gson();
        try (
                InputStream resource = cl.getResourceAsStream("example/glossary.json");
                InputStreamReader reader = new InputStreamReader(resource)
        ) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            assertThat(jsonObject.get("title").getAsString()).isEqualTo("example glossary");
            assertThat(jsonObject.get("GlossDiv").getAsJsonObject().get("title").getAsString()).isEqualTo("S");
            assertThat(jsonObject.get("GlossDiv").getAsJsonObject().get("flag").getAsBoolean()).isTrue();
        }
    }

    @Test
    @DisplayName("Проверка содержимого json файла, с применением pojo-класса, взятого из classPath при помощи ClassLoader")
    void jsonWithModelParseTest() throws Exception {
        Gson gson = new Gson();
        try (
                InputStream resource = cl.getResourceAsStream("example/glossary.json");
                InputStreamReader reader = new InputStreamReader(resource)
        ) {
            Glossary jsonObject = gson.fromJson(reader, Glossary.class);
            assertThat(jsonObject.title).isEqualTo("example glossary");
            assertThat(jsonObject.glossDiv.title).isEqualTo("S");
            assertThat(jsonObject.glossDiv.flag).isTrue();
        }
    }
}
