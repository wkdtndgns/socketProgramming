package com.chatting.projectchatting.client;

import com.chatting.projectchatting.domain.Message;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
public class ClientApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    private Client client = null;
    private static Map<String,Integer> roomMap = new HashMap<>();

    private RadioButton pinkButton;
    private RadioButton greenButton;
    private RadioButton blueButton;
    private RadioButton whiteButton;

    private Slider opacitySlider;

    private RadioButton smallTextButton;
    private RadioButton mediumTextButton;
    private RadioButton largeTextButton;

    protected void onChangeBackgroundColor(Color color, Stage stage) {
        // 배경색 변경
        Region root = (Region) stage.getScene().getRoot();
        root.setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(color, null, null)));
    }

    @Override
    public void start(Stage stage) throws Exception {
        File parentDir = new File("./chattingLog");
        stage.setOnCloseRequest(e->{
            System.exit(0);
        });

        TabPane tabPane = new TabPane();
        //-----------------------------------------------------
        // Tab 1: 방 선택
        Tab tab1 = new Tab("방 선택");
        VBox tab1Root = new VBox();
        Button btnRefrsh = new Button("새로고침");
        Button btn1 = new Button("접속");
        Button btn2 = new Button("데이터 전송");

        Text text = new Text("닉네임");
        TextArea textArea = new TextArea();
        TextField senderField = new TextField();
        ListView<String> userList = new ListView<>();
        userList.setPrefSize(100, 100);

        // 랜덤생성 닉네임
        Button randomGenerateNickBtn = new Button("랜덤 닉네임 생성");
        randomGenerateNickBtn.setOnAction(actionEvent -> senderField.setText(NicknameGenerator.randomNicknameGenerate()));

        HBox subRoot = new HBox();
        ListView<String> roomList = new ListView<>();
        ObservableList<String> rooms = FXCollections.observableArrayList();
        this.setRoom(roomList, rooms);
        tab1Root.setPadding(new Insets(10, 10, 10, 10));
        tab1Root.setSpacing(10);

        subRoot.getChildren().addAll( text, senderField, randomGenerateNickBtn, btn1);
        tab1Root.getChildren().addAll(subRoot,btnRefrsh,roomList);

        tab1.setContent(tab1Root);

        //-----------------------------------------------------
        // Tab 2: 채팅방
        Tab tab2 = new Tab("채팅방");
        VBox tab2Root = new VBox();

        HBox textRoot = new HBox();
        tab2Root.setPrefSize(600, 500);
        tab2Root.setSpacing(10);
        tab2Root.setPadding(new Insets(10, 0, 0, 0));
        subRoot.setSpacing(10);
        textRoot.setSpacing(10);

        TextField textField = new TextField();
        textArea.setEditable(false);
        textArea.setPrefSize(500,350);
        btn2.setDisable(true);
        tab2Root.setSpacing(10);

        Button quitRoomButton = new Button("방 나가기");

        // 매크로
        HBox macro = new HBox();
        macro.setSpacing(5);
        Button okBtn = new Button("네.");
        Button noBtn = new Button("아니요.");
        Button thanksBtn = new Button("감사합니다.");
        Button hardBtn = new Button("고생하셨습니다.");
        Button afterCallBtn = new Button("잠시 후 연락드리겠습니다.");
        Button callBtn = new Button("전화주세요.");
        Button restBtn = new Button("쉬었다가 합시다.");
        Button exportBtn = new Button("내보내기");
        Button importBtn = new Button("가져오기");
        Button dropOutBtn = new Button("강퇴 요청");
        okBtn.setOnAction(actionEvent -> client.send(senderField.getText(), okBtn.getText()));
        noBtn.setOnAction(actionEvent -> client.send(senderField.getText(), noBtn.getText()));
        thanksBtn.setOnAction(actionEvent -> client.send(senderField.getText(), thanksBtn.getText()));
        hardBtn.setOnAction(actionEvent -> client.send(senderField.getText(), hardBtn.getText()));
        afterCallBtn.setOnAction(actionEvent -> client.send(senderField.getText(), afterCallBtn.getText()));
        restBtn.setOnAction(actionEvent -> client.send(senderField.getText(), restBtn.getText()));
        callBtn.setOnAction(actionEvent -> client.send(senderField.getText(), callBtn.getText()));
        macro.getChildren().addAll(okBtn, noBtn, thanksBtn, hardBtn, afterCallBtn,callBtn,  restBtn);
        //매크로 end


        // 이모지 리스트
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(
                "\uD83D\uDE00", // Grinning face
                "\uD83D\uDE02", // Face with tears of joy
                "\uD83D\uDE1C", // Face with tongue sticking out
                "\uD83D\uDE0D", // Heart eyes face
                "\uD83D\uDE2D"  // Crying face
        );

        comboBox.setOnAction(event -> {
            String selectedEmoticon = comboBox.getValue();
            client.send(senderField.getText(), selectedEmoticon);
        });


        textRoot.getChildren().addAll(textField, comboBox, btn2, exportBtn, importBtn, dropOutBtn);
        tab2Root.getChildren().addAll( userList, textArea, textRoot,macro,quitRoomButton);

        exportBtn.setOnAction(actionEvent -> {
            String contents = textArea.getText().toString();
            LocalDateTime now = LocalDateTime.now();
            String formateNow = now.format(DateTimeFormatter.ofPattern("MM_dd_HH_mm"));
            System.out.println(parentDir.getPath());
            String fileName = parentDir.getPath() + "/" + formateNow+"_"+roomList.getSelectionModel().getSelectedItem()+".txt";

            if (!parentDir.exists()){
                parentDir.mkdir();
                System.out.println("chattingLog 디렉토리 생성");
            }
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(fileName));
                System.out.println("저장 완료");
                writer.write(contents);
                writer.close();
//                textArea.setText(textArea.getText() + "\n" + "대화 내용 저장 완료 : " + fileName);
                textArea.appendText("대화 내용 저장 완료 : " + fileName);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        importBtn.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("파일 선택");
            fileChooser.setInitialDirectory(new File("./chattingLog"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null){
                System.out.println("선택한 파일"+selectedFile.getPath());
                File file = new File(selectedFile.getPath());
                try (BufferedReader br = new BufferedReader(new FileReader(file))){
                    String line;
                    String result = "";
                    while ((line=br.readLine())!=null){
                        result+=line+"\n";
                    }
                    textArea.setText("이전 채팅을 불러옵니다.\n" +result+ "\n" +textArea.getText());
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        dropOutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String userName = userList.getSelectionModel().getSelectedItem();
                String sender = senderField.getText();
                client.send(Message.out(sender, userName));
            }
        });

        // 텍스트 엔터 누를 시 보내기
        textField.setOnAction(actionEvent -> {
            client.send(senderField.getText(), textField.getText());
            textField.setText("");
        });

//        textRoot.getChildren().addAll(textField, comboBox, btn2, exportBtn, importBtn);
//        tab2Root.getChildren().addAll( textArea, textRoot,macro,quitRoomButton);
        tab2Root.setPadding(new Insets(10, 10, 10, 10));

        tab2.setContent(tab2Root);

        // Tab 3: 설정
        Tab tab3 = new Tab("설정");
        VBox tab3Root = new VBox();
        tab3Root.setPadding(new Insets(10, 10, 10, 10));
        tab3Root.setSpacing(10);

        Text colorText = new Text("Change Background Color");
        colorText.setFont(Font.font("Arial",FontWeight.BOLD, 15));
        ToggleGroup colorGroup = new ToggleGroup();
        pinkButton = new RadioButton("pink color");
        pinkButton.setToggleGroup(colorGroup);
        pinkButton.setOnAction(e -> onChangeBackgroundColor(Color.web("#FEE8F6"), stage));
        greenButton = new RadioButton("green color");
        greenButton.setToggleGroup(colorGroup);
        greenButton.setOnAction(e -> onChangeBackgroundColor(Color.web("#F7FEE8"), stage));
        blueButton = new RadioButton("blue color");
        blueButton.setToggleGroup(colorGroup);
        blueButton.setOnAction(e -> onChangeBackgroundColor(Color.web("#E8F1FE"), stage));
        whiteButton = new RadioButton("white color");
        whiteButton.setToggleGroup(colorGroup);
        whiteButton.setOnAction(e -> onChangeBackgroundColor(Color.web("#FFFFFF"), stage));
        whiteButton.setSelected(true);

        Text opacityText = new Text("Change Background Opacity");
        opacityText.setFont(Font.font("Arial",FontWeight.BOLD, 15));
        opacitySlider = new Slider(0,100,100);
        opacitySlider.setMaxWidth(200);
        opacitySlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        stage.setOpacity(newValue.doubleValue() / 100);
                    }
                }
        );

        Text fontWeightText = new Text("Change Font Size");
        fontWeightText.setFont(Font.font("Arial",FontWeight.BOLD, 15));
        ToggleGroup fontWeightGroup = new ToggleGroup();
        smallTextButton = new RadioButton("small size");
        smallTextButton.setToggleGroup(fontWeightGroup);
        smallTextButton.setOnAction(e->textArea.setFont(Font.font("System", FontWeight.NORMAL, 10)));
        mediumTextButton = new RadioButton("medium size");
        mediumTextButton.setToggleGroup(fontWeightGroup);
        mediumTextButton.setOnAction(e->textArea.setFont(Font.font("System", FontWeight.NORMAL, 15)));
        largeTextButton = new RadioButton("large size");
        largeTextButton.setToggleGroup(fontWeightGroup);
        largeTextButton.setOnAction(e->textArea.setFont(Font.font("System", FontWeight.NORMAL, 20)));
        mediumTextButton.setSelected(true);

        Image img = new Image("file:./pngegg.png");
        ImageView imgView = new ImageView();
        imgView.setImage(img);
        imgView.setFitWidth(100);
        imgView.setFitHeight(100);

        tab3Root.getChildren().addAll(colorText,whiteButton, pinkButton, greenButton, blueButton, new Region(),opacityText, opacitySlider, new Region(),fontWeightText, smallTextButton, mediumTextButton, largeTextButton, imgView);
        tab3Root.setSpacing(10);
        tab3.setContent(tab3Root);
        //----------------------------------------------------

        // 탭을 닫을 수 없게 설정
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tab4 = new Tab("검색");
        VBox tab4Root = new VBox();

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load("https://www.google.com");

        tab4Root.getChildren().addAll(webView);
        tab4.setContent(tab4Root);
        // 탭 추가
        tabPane.getTabs().addAll(tab1, tab2, tab3,tab4);

        Scene scene = new Scene(tabPane);
        stage.setScene(scene);
        stage.setTitle("클라이언트");
        stage.show();


        // 이벤트 리스너

        // 접속 이벤트
        btn1.setOnAction(event -> {
            String roomName = roomList.getSelectionModel().getSelectedItem();
            String nick = senderField.getText();
             if (nick.isEmpty() || roomName.isEmpty()) {
                 Alert alert = new Alert(Alert.AlertType.ERROR);
                 alert.setTitle("경고");
                 alert.setHeaderText(null);
                 alert.setContentText("닉네임과 방을 확인해주세요.");
                 alert.showAndWait();
                 senderField.requestFocus();
             } else {
                 Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                 alert.setTitle("접속");
                 alert.setHeaderText("접속하시겠습니까?");
                 if(alert.showAndWait().get() == ButtonType.OK) {
                     int port = roomMap.get(roomName);
                     client = new Client(btn1, btn2, textArea, port, senderField.getText(), userList);
                     client.start();
                     tabPane.getSelectionModel().select(1);
                     textField.requestFocus();
                     stage.setTitle("방 :" + roomName);
                     senderField.setEditable(false);
                     randomGenerateNickBtn.setDisable(true);

                 }
             }
         });

         // 텍스트 전송
         btn2.setOnAction(event -> {
            client.send(senderField.getText(), textField.getText());
            textField.setText("");
        });

        // 새로고침 이벤트
        btnRefrsh.setOnAction(event -> {
            this.setRoom(roomList, rooms);
       });

        // 방나가기
        quitRoomButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("방 나가기");
            alert.setHeaderText("나가시겠습니까?");
            if(alert.showAndWait().get() == ButtonType.OK) {
                //disconnect 호출
                textField.setText("");
                textArea.setText("");
                btn1.setDisable(false);
                senderField.setEditable(true);
                randomGenerateNickBtn.setDisable(false);
                tabPane.getSelectionModel().select(0);
                btn2.setDisable(true);
                userList.getItems().clear();
                client.close();
            }
        });
    }

    /**
     * 방 세팅
     * @param roomList
     * @param rooms
     */
    private void setRoom(ListView<String> roomList, ObservableList<String> rooms){
        try {
            Reader r = new FileReader("server.txt");
            BufferedReader br = new BufferedReader(r);
            rooms.clear();
             // 한 줄씩 읽기
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split("\\|\\s*");
                rooms.add(arr[1]);
                roomMap.put(arr[1],Integer.valueOf(arr[0]));
            }
            roomList.setItems(rooms);
            br.close();
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
