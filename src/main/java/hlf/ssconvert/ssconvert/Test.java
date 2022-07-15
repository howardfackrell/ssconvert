package hlf.ssconvert.ssconvert;

public class Test {

  public static void main(String[] args) {
    String template = ":shortDesc";
    String shortDesc = "Vale-presente eletr. da GlobalLiving U$sdf";

    try {
    String result = template.replaceAll(":shortDesc", shortDesc);
    System.out.println(result);
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}
