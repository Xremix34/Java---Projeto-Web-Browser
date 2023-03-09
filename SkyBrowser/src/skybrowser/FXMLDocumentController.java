/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skybrowser;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.stage.Stage;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mcavallo.opencloud.Cloud;
import pt.ubi.hultig.LinguisticModel;

/**
 * @author Xremix30
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label lbLoad;  // Label que faz carregar o endereço
    @FXML
    private TextField txtURL; // text field de inserção do url
    @FXML
    WebView webView; // painel de visualização web, gere web engine
    private WebEngine webengine; // motor de gestão web (conexão html, css e javascript)
    @FXML
    BorderPane browserBP; //painel responsivo
    @FXML
    private ComboBox historyList;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnForward;
    @FXML
    private Button btnReload;
    @FXML
    private Button btnGo;
    @FXML
    private MenuItem Sair;
    @FXML
    private Button sentiment;
    @FXML
    private Button analysis;
    @FXML
    private Button tagCloud;

    @FXML
    private void goAction(ActionEvent event) {
        webengine.load(txtURL.getText().startsWith("http://") ? txtURL.getText() : "http://" + txtURL.getText());
        /*operador ternario para gettar texto do text field txtURL, verificar se a string começar com "http", e dar
        load ao web engine*/
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // webengine controlador de java para controlar uma pag web de cada vez
        webengine = webView.getEngine(); //web engine é igual ao web engine object retornado pela webview
        WebHistory history = webView.getEngine().getHistory();
        //ObservableList<Entry> entries = history.getEntries();
        webengine.locationProperty().addListener(new ChangeListener<String>() { //web engine.url da web page atual.adiciona um listener à string do url
            //sobreposição a qualquer linha de código(a primeira coisa a ser lida)
            @Override
            //Função para controlar o URL a carregar, damos um novo URL para substituir o velho
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //observa o valor do listener, verifica a string, verifica se o valor da string observado é igual ao anterior, caso contrário...
                txtURL.setText(newValue); //aplica o novo valor ao textfield do url..
                lbLoad.setText("Loaded " + txtURL.getText());//e dá load 
            }
        });
        history.getEntries().addListener(new ListChangeListener<WebHistory.Entry>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Entry> c) {
                c.next();
                for (Entry e : c.getRemoved()) {
                    historyList.getItems().remove(e.getUrl());
                }
                for (Entry e : c.getAddedSubList()) {
                    historyList.getItems().add(e.getUrl());
                }
            }
        }
        );
        historyList.setPrefWidth(100);
        historyList.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent ev) {
                int offset = historyList.getSelectionModel().getSelectedIndex() - history.getCurrentIndex();
                history.go(offset);
                /*operador ternario para gettar texto do text field txtURL, verificar se a string começar com "http", e dar
        load ao web engine*/
                lbLoad.setText("Loaded " + txtURL.getText());
            }
        });
        //Home
        txtURL.setText("http://www.google.com");//set text da página home/inicial
        //Pagina carregada ao inicializar o browser
        webengine.load(txtURL.getText());
        //webengine.carregar(textfield do url.gettar o texto.)

    }

    @FXML
    private void btnBack(ActionEvent event) {
        Tooltip tt = new Tooltip();
        btnBack.setTooltip(tt);
        if (webView.getEngine().getHistory().getCurrentIndex() <= 0) {
            return;
        }
        webView.getEngine().getHistory().go(-1);
    }

    @FXML
    private void btnForward(ActionEvent event) {
        if ((webView.getEngine().getHistory().getCurrentIndex() + 1) >= webView.getEngine().getHistory().getEntries().size()) {
            return;
        }
        webView.getEngine().getHistory().go(1);
    }

    @FXML
    private void btnReload(ActionEvent event) {
        if (webView.getEngine().getLoadWorker().isRunning()) {
            webView.getEngine().getLoadWorker().cancel();
            lbLoad.setText("Loaded " + txtURL.getText());
        } else {
            webView.getEngine().reload();
        }
    }

    @FXML
    private void btnHome(ActionEvent event) {
        webView.getEngine().loadContent("<html><title>New Tab</title></html>");
        txtURL.setText("http://www.google.com");
        webengine.load(txtURL.getText());
    }

    @FXML
    private void btnSair(ActionEvent event) throws IOException {

    }

    @FXML
    public void sentiment(ActionEvent event) throws IOException {
        BorderPane border = new BorderPane();
        border.setStyle("-fx-background-color:  #34495e");
        border.setPadding(new Insets(5));
        Label label = new Label();

        border.setTop(label);
        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane.setMargin(label, new Insets(5));
        label.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        label.setStyle(" -fx-text-fill: white");
        Slider slide = new Slider();
        slide.setMin(-50);
        slide.setMax(50);
        slide.setValue(0);
        slide.setDisable(true);
        slide.setMajorTickUnit(0.50f);
        slide.setBlockIncrement(0.1f);

        //Parte de fazer o Sumariado
        try {
            String url = txtURL.getText();
            Document doc = Jsoup.connect(url).get();
            Elements paragraphs = doc.select("p");
            ArrayList<String> frases = new ArrayList<>();
            ArrayList<String> palavras = new ArrayList<>();
            ArrayList<Double> valor = new ArrayList<>();
            double somaSentimentos = 0;

            LinguisticModel valorPalavra = new LinguisticModel(palavras);
            //preeenche o arraylist de frases fazendo split a cada ponto final
            for (Element p : paragraphs) {
                String paragrafos = p.text();
                String[] arrayfrase = paragrafos.split("\\.");
                Collections.addAll(frases, arrayfrase);
            }
            //limpa todas todos os caracteres especias das frases transformando-as em palavras 
            for (String f : frases) {
                String[] arrayfrase = f.split("[-,.;!?+´`*»?=/&%$#!€<> ]");
                Collections.addAll(palavras, arrayfrase);
            }

            //Soma todos os valores de cada palavra   
            for (String vPal : palavras) {
                System.out.println(vPal + valorPalavra.getSentiment(vPal));
                somaSentimentos += valorPalavra.getSentiment(vPal);
                slide.setValue(5 + somaSentimentos);
            }

            //condições para slide de sentimentos
            if (slide.getValue() > Integer.MIN_VALUE && slide.getValue() <= -20) {
                Image img = new Image("file:src\\resources\\imagemSent\\muitoTriste.png");
                ImageView imgView = new ImageView(img);
                border.setCenter(imgView);
                label.setText("Muito Triste");
            } else if (slide.getValue() > -20 && slide.getValue() <= 0) {
                Image img = new Image("file:src\\resources\\imagemSent\\triste.png");
                ImageView imgView = new ImageView(img);
                border.setCenter(imgView);
                label.setText("Triste");

            } else if (slide.getValue() > 0 && slide.getValue() <= 20) {
                Image img = new Image("file:src\\resources\\imagemSent\\normal.png");
                ImageView imgView = new ImageView(img);
                border.setCenter(imgView);

                label.setText("Neutro");

            } else if (slide.getValue() > 20 && slide.getValue() <= 30) {
                Image img = new Image("file:src\\resources\\imagemSent\\contente.png");
                ImageView imgView = new ImageView(img);
                imgView.setStyle("-fx-background-color:  #34495e");
                border.setCenter(imgView);
                label.setText("Feliz");

            } else if (slide.getValue() > 30 && slide.getValue() <= Integer.MAX_VALUE) {
                Image img = new Image("file:src\\resources\\imagemSent\\muitoFeliz.png");
                ImageView imgView = new ImageView(img);
                border.setCenter(imgView);
                label.setText("Muito Feliz");
            }

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

        border.setBottom(slide);
        Stage stage = new Stage();
        Scene scene = new Scene(border, 300, 300);
        stage.setScene(scene);
        stage.show();
    }

    //Função para criar a TagCloud
    @FXML
    public void tagCloud(ActionEvent event) throws IOException {
        String[] palavras;
        ArrayList<Double> valores = new ArrayList<>();
        ArrayList<String> palavrasCloud = new ArrayList<>();
        String bigString = "";
        JFrame frame = new JFrame("TagCloud");
        JPanel panel = new JPanel();
        Cloud cloud = new Cloud();

        try {
            String url = txtURL.getText();
            Document doc = Jsoup.connect(url).get();
            Elements paragraphs = doc.select("p");

            for (Element p : paragraphs) {
                String frases = p.text();
                String[] frasesP = frases.split("\\.");
                for (int i = 0; i < frasesP.length; i++) {
                    //System.out.println(frasesP[i]);
                    bigString = bigString + " " + frasesP[i];
                    //palavras = frasesP.split("[-,.;!?+´`*»?=/&%$#!€<> ]");
                }
            }

            palavras = bigString.split("[-,.;:!?+´`*»?=/&%$#!€<>\" ]");
            LinguisticModel valorPalavra = new LinguisticModel(palavras);

            for (int i = 0; i < palavras.length; i++) {
                valores.add(valorPalavra.getWordValue(palavras[i]));

            }

            for (int i = 0; i < valores.size(); i++) {
                if (valores.get(i) > 0.7) {
                    palavrasCloud.add(palavras[i]);
                }
            }

            for (int i = 0; i < palavrasCloud.size(); i++) {
                System.err.println(palavrasCloud.get(i));
            }

            for (String s : palavrasCloud) {
                cloud.addTag(s);
            }

            for (org.mcavallo.opencloud.Tag tag : cloud.tags()) {
                final JLabel label = new JLabel(tag.getName());
                label.setOpaque(false);
                label.setFont(label.getFont().deriveFont((float) tag.getWeight() * 10));
                panel.add(label);
            }
            frame.add(panel);
            frame.setSize(800, 600);
            frame.setVisible(true);

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    //Função para Sumariar o texto de um site
    @FXML
    private void summary(ActionEvent event) throws IOException {

        BorderPane root1 = new BorderPane();
        root1.setStyle("-fx-background-color: #34495e;");
        root1.setPadding(new Insets(5));
        Label title = new Label("SUMMARY");
        title.setStyle("-fx-font-size: 25px; -fx-text-fill: white;");
        BorderPane.setAlignment(title, Pos.CENTER);
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        root1.setTop(title);
        title.setMaxHeight(Double.POSITIVE_INFINITY);
        title.setTextAlignment(TextAlignment.CENTER);
        TextArea textArea = new TextArea();
        textArea.setStyle("-fx-background-color:  #34495e");
        textArea.setWrapText(true);
        textArea.setEditable(false);
        root1.setCenter(textArea);
        Stage stage = new Stage();
        Scene scene = new Scene(root1, 400, 300);
        stage.setScene(scene);
        stage.show();

        try {
            String url = txtURL.getText();
            Document doc = Jsoup.connect(url).get();
            Elements paragraphs = doc.select("p");
            ArrayList<String> frases = new ArrayList<>();

            ArrayList<Double> valorFrases = new ArrayList<>();
            double x = 0, y = 0, z = 0, a = 0, b = 0, c = 0;
            int pos_x = 0, pos_y = 0, pos_z = 0, pos_a = 0, pos_b = 0, pos_c = 0;
            LinguisticModel valorPalavra;

            double somaValores = 0;
            //preeenche o arraylist de frases fazendo split a cada ponto final
            for (Element p : paragraphs) {
                String paragrafos = p.text();
                String[] frase = paragrafos.split("\\.");
                Collections.addAll(frases, frase);
            }

            // Adiciona a soma de todos os valores das palavras à variavél somaValores
            for (int i = 0; i < frases.size(); i++) {
                String[] frasePorPalavras = frases.get(i).split("[-,.;!?+´`*»?=/&%$#!€<> ]");
                for (int j = 0; j < frasePorPalavras.length; j++) {
                    valorPalavra = new LinguisticModel(frasePorPalavras);
                    somaValores = somaValores + valorPalavra.getWordValue(frasePorPalavras[j]);
                    //System.out.println(somaValores);
                }
                valorFrases.add(somaValores);

                somaValores = 0;
            }

            //acha a posição das 5 frases com mais valor
            for (int i = 0; i < valorFrases.size(); i++) {
                if (valorFrases.get(i) > x) {
                    x = valorFrases.get(i);
                    pos_x = i;
                }
            }
            for (int i = 0; i < valorFrases.size(); i++) {
                if (valorFrases.get(i) > y && valorFrases.get(i) < x) {
                    y = valorFrases.get(i);
                    pos_y = i;
                }
            }
            for (int i = 0; i < valorFrases.size(); i++) {
                if (valorFrases.get(i) > z && valorFrases.get(i) < x && valorFrases.get(i) < y) {
                    z = valorFrases.get(i);
                    pos_z = i;
                }
            }
            for (int i = 0; i < valorFrases.size(); i++) {
                if (valorFrases.get(i) > a && valorFrases.get(i) < z && valorFrases.get(i) < x && valorFrases.get(i) < y) {
                    a = valorFrases.get(i);
                    pos_a = i;
                }
            }

            for (int i = 0; i < valorFrases.size(); i++) {
                if (valorFrases.get(i) > b && valorFrases.get(i) < a && valorFrases.get(i) < z && valorFrases.get(i) < x && valorFrases.get(i) < y) {
                    b = valorFrases.get(i);
                    pos_b = i;
                }
            }

            for (int i = 0; i < valorFrases.size(); i++) {
                if (valorFrases.get(i) > c && valorFrases.get(i) < b && valorFrases.get(i) < a && valorFrases.get(i) < z && valorFrases.get(i) < x && valorFrases.get(i) < y) {
                    c = valorFrases.get(i);
                    pos_c = i;
                }
            }

            //Adiciona as 5 frases com mais valor (achadas anteriormente) ao text area
            textArea.appendText(frases.get(pos_x));
            textArea.appendText(".");
            textArea.appendText("\n");
            textArea.appendText(frases.get(pos_y));
            textArea.appendText(".");
            textArea.appendText("\n");
            textArea.appendText(frases.get(pos_z));
            textArea.appendText(".");
            textArea.appendText("\n");
            textArea.appendText(frases.get(pos_a));
            textArea.appendText(".");
            textArea.appendText("\n");
            textArea.appendText(frases.get(pos_b));
            textArea.appendText(".");
            textArea.appendText("\n");
            textArea.appendText(frases.get(pos_c));
            textArea.appendText(".");

            System.out.println(frases.get(pos_x));
            System.out.println(frases.get(pos_y));
            System.out.println(frases.get(pos_z));
            System.out.println(frases.get(pos_a));
            System.out.println(frases.get(pos_b));
            System.out.println(frases.get(pos_c));

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
