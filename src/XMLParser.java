// Example of parsing data from web in JAVA
// Parse site of Central Bank of Russia and show information about most expensive and cheep currency at current date

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLParser {
    String [][] attributes = new String[2][6]; // массив значений для самых дешевых и самых дорогих валют

    public static void main(String []args){
        XMLParser object_test = new XMLParser();
        object_test.get_response();
    }

    public void get_response(){
        try {

            String ValueString; // cтрока считываемая для значения курса валюты
            String NominalString; // строка считываемая для значения номинала валюты
            String bufferStringForDouble = ""; // буфферная строка для перевода в значение с плавающей точкой

            double maximum = 0.0; // переменная для вычисления максимума
            double minimum = 0.0; // переменная для вычисления минимума
            double buffer; // переменная для расчета курсам валюты к единице иностранной валюты

            String url = "http://www.cbr.ru/scripts/XML_daily.asp";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            InputSource is = new InputSource(new StringReader(response.toString()));
            is.setEncoding("ISO-8859-1");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

            NodeList Nodes = doc.getElementsByTagName("Valute");

            if (Nodes.getLength() > 0)
            {
                minimum = CountNominalToOneRuble(Nodes, 0); // присваиваем первый минимум первой валюты в качестве исходника

                for (int i = 0; i < Nodes.getLength(); i++)
                {
                    buffer = CountNominalToOneRuble(Nodes, i); // считаем курс к рублю очередной валюты из списка
                    Element elementOfNode = (Element) Nodes.item(i);

                    if (buffer >= maximum) {
                        maximum = buffer; // если следующее значение больше буффера
                        WriteIntoFinalArray(elementOfNode, 0); // записываем данные дорогой валюты в массив
                    }

                    if (buffer <= minimum) {
                        minimum = buffer;
                        WriteIntoFinalArray(elementOfNode, 1); // записываем данные дешевой валюты в массив
                    }
                }

                // выводим информацию в консоль
                for (int i = 0; i < 2; i++) {
                    for(int j = 0; j < 6; j++) {
                        System.out.println(attributes[i][j]);
                    }
                    System.out.println(""); // перевод на новую строку, чтобы не сливалось
                }
            }

        } catch (Exception e) {
            System.out.println("error777");
        }
    }

    // функция для рассчета курса валюты к одному рублю
    // парсит значения валюты и номинала, переводит в double и возвращает результат
    public double CountNominalToOneRuble(NodeList nodes, int numberOfNode){
        Element elementOfNode = (Element) nodes.item(0);
        elementOfNode = (Element) nodes.item(numberOfNode);
        String bufferStringForDouble = elementOfNode.getElementsByTagName("Value").item(0).getTextContent();
        String ValueString = bufferStringForDouble.replace(",", ".");
        bufferStringForDouble = elementOfNode.getElementsByTagName("Nominal").item(0).getTextContent();
        String NominalString = bufferStringForDouble.replace(",", ".");
        return Double.parseDouble(ValueString) / Double.parseDouble(NominalString);
    }

    // функция записи данных по максимальной и минимальной валюте в справочный массив
    public void WriteIntoFinalArray (Element element, int MinMaxNumber) {
        attributes[MinMaxNumber][0] = "Самая ДЕШЕВАЯ валюта (к рублю): "; // значение массива
        attributes[MinMaxNumber][1] = "Код валюты: " + element.getElementsByTagName("NumCode").item(0).getTextContent(); // код валюты
        attributes[MinMaxNumber][2] = "Обозначение латиницей: " + element.getElementsByTagName("CharCode").item(0).getTextContent(); // код валюты латиницей
        attributes[MinMaxNumber][3] = "Номинал: " + element.getElementsByTagName("Nominal").item(0).getTextContent(); // номинал
        attributes[MinMaxNumber][4] = "Наименование валюты: " + element.getElementsByTagName("Name").item(0).getTextContent(); // имя валюты
        attributes[MinMaxNumber][5] = "Курс валюты: " + element.getElementsByTagName("Value").item(0).getTextContent() + " руб."; // курс валюты
    }
}
