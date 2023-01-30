package study.qa;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SelenideFilesTest {

    // для файлов, которые скачиваются не через href
//    static {
//        Configuration.fileDownload = FileDownloadMode.PROXY;
//    }

    @Test
    void selenideDownloadTest() throws Exception {
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        File downloadedFile = $("#raw-url").download();
        try (InputStream is = new FileInputStream(downloadedFile)) {
            byte[] bytes = is.readAllBytes();
            String textContent = new String(bytes, StandardCharsets.UTF_8);
            assertThat(textContent).contains("This repository is the home of the next generation of JUnit, _JUnit 5_.");
        }

    }

    @Test
    void selenideUploadTest() {
        open("https://fineuploader.com/demos.html");
        $("input[type='file").uploadFromClasspath("example/cat_notebook.jpeg");
        $("div.qq-file-info").shouldHave(text("cat_notebook.jpeg"));
        $("span.qq-upload-file-selector")
                .shouldHave(attribute("title", "cat_notebook.jpeg"));
    }
}
