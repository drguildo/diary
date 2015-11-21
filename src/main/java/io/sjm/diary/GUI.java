/*
 * Copyright (c) 2015, Simon Morgan
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package io.sjm.diary;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class GUI extends Stage {
    private DatePicker datePicker = new DatePicker();
    private Entry entry;
    private String password = "";

    public GUI() {
        BorderPane mainLayout = new BorderPane();
        HBox hBox = buildMenuBar(datePicker);
        TextArea textArea = new TextArea();

        // password = getPassword();

        addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.W) {
                exit();
            }
        });

        addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.S) {
                entry.setText(textArea.getText());
                try {
                    entry.save();
                } catch (IOException ex) {
                    ExceptionDialog ed = new ExceptionDialog(ex);
                    ed.showAndWait();
                }
            }
        });

        datePicker.setOnAction(e -> {
            try {
                entry = new Entry(datePicker.getValue());
                textArea.setText(entry.getText());
            } catch (IOException ex) {
                ExceptionDialog ed = new ExceptionDialog(ex);
                ed.showAndWait();
            }
        });

        mainLayout.setTop(hBox);
        mainLayout.setCenter(textArea);

        setTitle("Diary");
        setScene(new Scene(mainLayout, 800, 600));

        try {
            entry = new Entry(LocalDate.now());
            textArea.setText(entry.getText());
        } catch (IOException ex) {
            ExceptionDialog ed = new ExceptionDialog(ex);
            ed.showAndWait();
        }
    }

    private HBox buildMenuBar(DatePicker datePicker) {
        HBox hBox = new HBox();

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem exitMenu = new MenuItem("Exit");
        Menu helpMenu = new Menu("Help");

        exitMenu.setOnAction(e -> exit());
        fileMenu.getItems().addAll(exitMenu);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

        datePicker.setValue(LocalDate.now());

        hBox.getChildren().addAll(menuBar, this.datePicker);
        HBox.setHgrow(menuBar, Priority.ALWAYS);

        datePicker.setOnAction(event -> {
            LocalDate date = datePicker.getValue();
            try {
                entry = new Entry(date);
            } catch (Exception ex) {
                ExceptionDialog ed = new ExceptionDialog(ex);
                ed.showAndWait();
            }
        });

        return hBox;
    }

    private String getPassword() {
        PasswordDialog pd = new PasswordDialog();
        Optional<String> result;

        result = pd.showAndWait();

        if (result.isPresent())
            return result.get();
        else
            exit();

        return null;
    }

    private void exit() {
        Platform.exit();
    }
}
