package hlf.ssconvert.ssconvert;


import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class SsconvertCliRunner implements CommandLineRunner {

  String locale = "sl_SI";

  @Override
  public void run(String... args) throws Exception {

//    String excelName = "Returned Portuguese(Brazil) translations required 7 11 22.xlsx";
//    String sqlName = "Returned Portuguese(Brazil) translations required 7 11 22.sql";
//    String excelName = "Returned Spanish(Mexico) translations required 7 11 22.xlsx";
//    String sqlName = "Returned Spanish(Mexico) translations required 7 11 22.sql";
    String excelName = "Slovenian translations required 7 7 22.xlsx";
    String sqlName = "Slovenian translations required 7 7 22.sql";

    Iterable<Map<String, Object>> rows =
        new SsSequenceDs(
                excelName,
                "partNumber",
                "enShortDesc",
                "enWebShortDesc",
                "enMedDesc",
                "enLongDesc",
                "shortDesc",
                "webShortDesc",
                "medDesc",
                "longDesc"
        )
            .read();

    List<Map<String, Object>> rowsList = IterableUtils.toList(rows);
    List<String> sqls = rowsList.stream()
            .filter(hasNumericParNumber)
            .map(this::generateSql)
            .collect(Collectors.toList());



    try(PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(new File(sqlName))))) {
      sqls.stream().forEach(sql -> {
        output.println(sql);
        output.println();
      });
      output.flush();
    }
  }

  String generateSql(Map<String, Object> translation) {
    String partNumber = sqlEscape((String) translation.get("partNumber"));
    partNumber = StringUtils.leftPad(partNumber, 18, "0");
    String shortDesc = sqlEscape((String) translation.get("shortDesc"));
    String webShortDesc = sqlEscape((String) translation.get("webShortDesc"));
    String medDesc = sqlEscape((String) translation.get("medDesc"));
    String longDesc = sqlEscape((String) translation.get("longDesc"));
//    String locale = "en_US";

    System.out.println(partNumber);
    String sqlTemplate =
            """
                    begin
                        insert into mmdescriptions (UNIQUEID, SAPPARTNUMBER, LOCALE, SHORTDESCRIPTION, WEBSHORTDESCRIPTION, MEDIUMDESCRIPTION, LONGDESCRIPTION)
                        values (MMDESCRIPTIONS_UNIQUEID_SEQ.nextval,
                                ':partNumber',
                                ':locale',
                                ':shortDesc',
                                ':webShortDesc',
                                ':medDesc',
                                ':longDesc');
                    exception
                        when dup_val_on_index then
                            update mmdescriptions
                            set SHORTDESCRIPTION  = ':shortDesc',
                                WEBSHORTDESCRIPTION = ':webShortDesc',
                                MEDIUMDESCRIPTION = ':medDesc',
                                LONGDESCRIPTION   = ':longDesc'
                            where sappartnumber = ':partNumber'
                              and locale = ':locale';
                    end;
                    """;
    
    String sql = sqlTemplate.replaceAll(":locale", locale)
            .replace(":partNumber", partNumber)
            .replace(":shortDesc", shortDesc)
            .replace(":webShortDesc", webShortDesc)
            .replace(":medDesc", medDesc)
            .replace(":longDesc", longDesc);
    return sql;
  }

  static Predicate<Map<String, Object>> hasNumericParNumber = new Predicate<Map<String, Object>>() {
    @Override
    public boolean test(Map<String, Object> map) {
      String partNumber = (String) map.get("partNumber");
      return StringUtils.isNumeric(partNumber);
    }
  };

  static String sqlEscape(String input) {
    input = input == null ? "" : input;
    return input.replaceAll("'", "''");
  }
}
