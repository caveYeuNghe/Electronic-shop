//Thanh vien xay dung: Dat

package Java.Controller;

import Java.Converter.CustomFloatStringConverter;
import Java.Converter.CustomIntegerStringConverter;
import Java.Converter.CustomStringStringConverter;
import Java.Model.Product.CellPhone;
import Java.Model.Product.Device;
import Java.Model.Product.Laptop;
import Java.Model.user.Admin;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;

public class AdminController extends Controller<Device> {

    @FXML   //nut them dien thoai
    private void addPhonePressed(ActionEvent event) throws SQLException {
        AddDeviceBox addDeviceBox = new AddDeviceBox();
        addDeviceBox.addPhone();
        super.deviceList.setAll(admin.getDevices());                //reset lai table
        updateSearchResult();                                       //cap nhat ket qua tim kiem
    }

    @FXML   //nut them laptop
    private void addLaptopPressed(ActionEvent event) throws SQLException {
        AddDeviceBox addDeviceBox = new AddDeviceBox();
        addDeviceBox.addLaptop();                           //goi addLapTop thuoc lop AddDeviceBox

        super.deviceList.setAll(admin.getDevices());        //cap nhat table
        super.tableDv.setItems(super.deviceList);

        updateSearchResult();                               //cap nhat ket qua tim kiem
    }

    @FXML   //nut xoa
    private void deletePressed(ActionEvent event) throws SQLException {
        Device item = super.tableDv.getFocusModel().getFocusedItem();   //lay item dang dc chon

        admin.Delete(item);                                             //xoa item tren database

        super.deviceList.setAll(admin.getDevices());                    //cap nhat table
        super.tableDv.setItems(super.deviceList);

        super.updateSearchResult();

    }

    @FXML   //nut cap nhat
    private void updatePressed(ActionEvent event) throws SQLException {
        for (Device device :
                this.changedItem) {

            admin.Modify(device);               //cap nhat tung thiet bi duoc thay doi
        }

        this.changedItem.clear();               //xoa cac thiet bi khoi danh sach cac thiet bi dc thay thois

        super.updateSearchResult();             //cap nhat ket qua tim kiem

        this.updateButton.setDisable(true);     //tat nu cap nhat cho den khi co thay doi
    }

    @FXML   // nut cap nhat
    private Button updateButton;

    @FXML
    private void revenuePressed(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream("/Resource/View/RevenueScene.fxml")); //load RevenueScene

        stage.initModality(Modality.APPLICATION_MODAL);                                                     //ko the tuong tac vs stage chinh
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.getStylesheets().add("/Resource/css/Style.css");
        stage.show();

    }

    @FXML
    private void userInfoPressed(ActionEvent event) throws IOException {
        Stage stage = new Stage(); //lay stage hien tai
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream("/Resource/View/UserInfoScene.fxml")); //load UserInfoScene

        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        scene.getStylesheets().add("/Resource/css/Style.css");
        stage.show();
    }


    private final Set<Device> changedItem = new LinkedHashSet<>();  //nhung thiet bi dc thay doi
    private Admin admin;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        admin = (Admin) super.user;
        try {
            super.deviceList.addAll(admin.getDevices());    //lay du lieu tu database
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        columnInit();                                       //khoi tao cot



        super.tableDv.setItems(deviceList);                 //khoi tao du lieu de table hien thi
        super.tableDv.setEditable(true);                    //cho phep thay doi gia tri trong bang

        super.updateSearchResult();                          //cap nhat ket qua tim kiem

        super.searchText.setOnKeyPressed(keyEvent -> {      //hien thi ket qua tim kiem khi nhan dau cach
            if(keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    super.searchHandle(deviceList);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    void columnInit(){
        super.columnInit();
        //cot id khong the thay doi

        //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/cell/TextFieldTableCell.html#forTableColumn--
        tenColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tenColumn.setOnEditCommit((EventHandler<TableColumn.CellEditEvent>) t -> {
            Device device = (Device) t.getTableView().getItems().get(
                    t.getTablePosition().getRow());                                         //lay thiet bi dc thay doi
            device.setTen((String) t.getNewValue());                                        //thay doi gia tri
            this.updateButton.setDisable(false);                                            //enable nut cap nhat
            changedItem.add(device);                                                        //them thiet bi vao danh sach thay doi
            updateSearchResult();                                                           //cap nhat ket qua tim kiem
        });

        hangSanXuatColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        hangSanXuatColumn.setOnEditCommit(event -> {
            Device device = event.getTableView().getItems().get(
                    event.getTablePosition().getRow());
            device.setHangSanXuat(event.getNewValue());
            this.updateButton.setDisable(false);
            changedItem.add(device);
            updateSearchResult();
        });


        modelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        modelColumn.setOnEditCommit(event -> {
            Device device = event.getTableView().getItems().get(
                    event.getTablePosition().getRow());
            device.setModel(event.getNewValue());
            this.updateButton.setDisable(false);
            changedItem.add(device);
        });

        //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/cell/TextFieldTableCell.html#forTableColumn-javafx.util.StringConverter-
        StringConverter<String> strConvert = new CustomStringStringConverter();       //doi dinh dang string sang string (vi EditCustomCell tu dinh nghia)
        StringConverter<Integer> intConvert = new CustomIntegerStringConverter();     //Intteger sang String va ngc lai
        StringConverter<Float> floatConvert = new CustomFloatStringConverter();       //Float sang String va ngc lai

        priceColumn.setCellFactory(e -> new TextFieldTableCell<>(intConvert){
            @Override
            public void updateItem(Integer integer, boolean b) {
                super.updateItem(integer, b);
                if(!b) {
                    setText(NumberFormat.getIntegerInstance(Locale.GERMAN).format(integer));    //hien dau ngan cach hang nghin
                }
            }
        });

        priceColumn.setOnEditCommit(event -> {
            int price;
            try {
                price = event.getNewValue();
                if (price <= 0)
                    throw new NumberFormatException("gia tien phai lon hon 0");                 //kiem soat input
            } catch (NumberFormatException e) {
                AlertBox.display("loi dinh dang", e.getMessage());
                price = event.getOldValue();
            }
            Device device = event.getTableView().getItems().get(                                //lay thiet bi duoc chon
                    event.getTablePosition().getRow());
            device.setPrice(price);                                                             //thay doi thuoc tinh
            event.getTableView().getItems().set(event.getTablePosition().getRow(), device);     //cap nhat giao dien(neu khong thi no se hien thi gia tri nhap sai)
            this.updateButton.setDisable(false);
            changedItem.add(device);
        });


        kichThuocColumn.setCellFactory(e -> new EditCustomCell(floatConvert));
        kichThuocColumn.setOnEditCommit(event -> {
            CellPhone phone = (CellPhone) event.getTableView().getItems().get(
                    event.getTablePosition().getRow());

            float kichThuoc;
            try {
                kichThuoc = event.getNewValue();
                if(kichThuoc <= (float) 0)
                    throw new NumberFormatException("kich thuoc phai lon hon 0");
            } catch (NumberFormatException e) {
                AlertBox.display("loi dinh dang", e.getMessage());
                kichThuoc = event.getOldValue();
            }
            phone.setKichThuoc(kichThuoc);
            event.getTableView().getItems().set(event.getTablePosition().getRow(), phone);
            this.updateButton.setDisable(false);
            changedItem.add(phone);
        });

        thoiLuongPinColumn.setCellFactory(e -> new EditCustomCell<>(intConvert));
        thoiLuongPinColumn.setOnEditCommit(event -> {
            CellPhone phone = (CellPhone) event.getTableView().getItems().get(
                    event.getTablePosition().getRow());
            int thoiLuongPin;
            try {
                thoiLuongPin = event.getNewValue();
                if(thoiLuongPin <= 0)
                    throw new NumberFormatException("thoi luong pin phai lon hon 0");
            } catch (NumberFormatException e) {
                AlertBox.display("Loi dinh dang", e.getMessage());
                thoiLuongPin = event.getOldValue();
            }
            phone.setThoiLuongPin(thoiLuongPin);
            event.getTableView().getItems().set(event.getTablePosition().getRow(), phone);
            this.updateButton.setDisable(false);
            changedItem.add(phone);
        });

        doPhanGiaiCameraColumn.setCellFactory(e -> new EditCustomCell<>(floatConvert));
        doPhanGiaiCameraColumn.setOnEditCommit(event -> {
            CellPhone phone = (CellPhone) event.getTableView().getItems().get(
                    event.getTablePosition().getRow());
            float dpg;
            try {
                dpg = event.getNewValue();
                if(dpg <= (float) 0)
                    throw new NumberFormatException("do phan giai phai lon hon 0");
            } catch (NumberFormatException e) {
                AlertBox.display("loi dinh dang", e.getMessage());
                dpg = event.getOldValue();
            }
            phone.setDoPhanGiaiCamera(dpg);
            event.getTableView().getItems().set(event.getTablePosition().getRow(), phone);
            this.updateButton.setDisable(false);
            changedItem.add(phone);
        });

        CPUColumn.setCellFactory(e -> new EditCustomCell<>(strConvert));
        CPUColumn.setOnEditCommit(event -> {
            Laptop laptop = (Laptop) event.getTableView().getItems().get(
                    event.getTablePosition().getRow());
            laptop.setCPU(event.getNewValue());
            this.updateButton.setDisable(false);
            changedItem.add(laptop);
        });

        RAMColumn.setCellFactory(e -> new EditCustomCell<>(intConvert));
        RAMColumn.setOnEditCommit(event -> {
            Laptop laptop = (Laptop) event.getTableView().getItems().get(
                    event.getTablePosition().getRow());
            int ram;
            try {
                ram = event.getNewValue();
                if(ram <= 0)
                    throw new NumberFormatException("ran phai lon hon 0");
            } catch (NumberFormatException e) {
                AlertBox.display("Loi dinh dang", e.getMessage());
                ram = event.getOldValue();
            }
            laptop.setRAM(ram);
            event.getTableView().getItems().set(event.getTablePosition().getRow(), laptop);
            this.updateButton.setDisable(false);
            changedItem.add(laptop);
        });

        hardDriveColumn.setCellFactory(e -> new EditCustomCell<>(strConvert));
        hardDriveColumn.setOnEditCommit(event -> {
            Laptop laptop = (Laptop) event.getTableView().getItems().get(
                    event.getTablePosition().getRow());
            laptop.setoCung(event.getNewValue());
            this.updateButton.setDisable(false);
            changedItem.add(laptop);
        });

        conLaiColumn.setCellFactory(e -> new EditCustomCell<>(intConvert));
        conLaiColumn.setOnEditCommit(event -> {
            Device device = event.getTableView().getItems().get(
                    event.getTablePosition().getRow());
            int conLai;
            try {
                conLai = event.getNewValue();
                if(conLai <= 0)
                    throw new NumberFormatException("Con lai phai lon hon 0");
            } catch (NumberFormatException e) {
                AlertBox.display("Loi dinh dang", e.getMessage());
                conLai = event.getOldValue();
            }
            device.setConLai(conLai);
            event.getTableView().getItems().set(event.getTablePosition().getRow(), device);
            this.updateButton.setDisable(false);
            changedItem.add(device);
        });


    }

    //neu thiet bi khong co thuoc tinh thi se hien thi la "NUll" va khong cho thay doi
    private static class EditCustomCell<T> extends TextFieldTableCell<Device, T> {
        public EditCustomCell(StringConverter<T> stringConverter) {
            super(stringConverter);
        }
        @Override
        public void updateItem(T obj, boolean empty) {
            super.updateItem(obj, empty);
            if (!empty) {
                if (obj == null) {
                    setText("NULL");
                    setEditable(false);
                }
                else
                    setText(obj.toString());
            }
            else
                setText(null);
        }
    }
}
