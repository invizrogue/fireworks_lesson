package study.qa.model;

import com.google.gson.annotations.SerializedName;

public class Glossary {
    public String title;
    @SerializedName("GlossDiv") // т.к. в json-файле использована заглавная буква в названии поля, а это не допускается в java
    public GlossDiv glossDiv;

    public static class GlossDiv {
        public String title;
        public boolean flag;
    }
}
