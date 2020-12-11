package io.github.palexdev.materialfx.validation;

import io.github.palexdev.materialfx.beans.binding.BooleanListBinding;
import io.github.palexdev.materialfx.controls.MFXStageDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.factories.MFXDialogFactory;
import io.github.palexdev.materialfx.utils.StringUtils;
import io.github.palexdev.materialfx.validation.base.AbstractMFXValidator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a concrete implementation of a validator.
 * <p>
 * This validator has a string message associated with every boolean property in its base class.
 * It can show a {@link MFXStageDialog} containing all warning messages.
 */
public class MFXDialogValidator extends AbstractMFXValidator {
    //================================================================================
    // Properties
    //================================================================================
    private final List<String> messages = new ArrayList<>();
    private final ObjectProperty<DialogType> dialogType = new SimpleObjectProperty<>(DialogType.WARNING);
    private String title;
    private MFXStageDialog stageDialog;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXDialogValidator(String title) {
        this.title = title;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        dialogType.addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue) && stageDialog != null) {
                MFXDialogFactory.convertToSpecific(newValue, stageDialog.getDialog());
                Label label = (Label) stageDialog.getDialog().lookup(".content-label");
                label.setAlignment(Pos.CENTER);
            }
        });
    }

    /**
     * Shows this validator's dialog. The dialog is not modal and doesn't scrim the background.
     */
    public void show() {
        if (stageDialog == null) {
            stageDialog = new MFXStageDialog(dialogType.get(), title, "");
            Label label = (Label) stageDialog.getDialog().lookup(".content-label");
            label.setAlignment(Pos.CENTER);
        }

        stageDialog.getDialog().setContent(getMessages());
        stageDialog.setOwner(null);
        stageDialog.setModality(Modality.NONE);
        stageDialog.setCenterInOwner(false);
        stageDialog.setScrimBackground(false);
        stageDialog.show();
    }

    /**
     * Shows this validator's dialog. The dialog is modal and scrims the background.
     * @param owner The dialog's owner.
     */
    public void showModal(Window owner) {
        if (stageDialog == null) {
            stageDialog = new MFXStageDialog(dialogType.get(), title, "");
            Label label = (Label) stageDialog.getDialog().lookup(".content-label");
            label.setAlignment(Pos.CENTER);
        }

        stageDialog.getDialog().setContent(getMessages());
        stageDialog.setOwner(owner);
        stageDialog.setModality(Modality.WINDOW_MODAL);
        stageDialog.setCenterInOwner(true);
        stageDialog.setScrimBackground(true);
        stageDialog.show();

    }

    /**
     * Adds a new boolean condition to the list with the corresponding message in case it is false.
     * @param property The new boolean condition
     * @param message The message to show in case it is false
     */
    public void add(BooleanProperty property, String message) {
        if (super.conditions == null || super.validation == null) {
            super.conditions = FXCollections.observableArrayList(property);
            super.validation = new BooleanListBinding(conditions);
        } else {
            super.conditions.add(property);
        }

        this.messages.add(message);
    }

    /**
     * Removes the given property and the corresponding message from the list.
     */
    public void remove(BooleanProperty property) {
        int index = conditions.indexOf(property);
        if (index != -1 && messages.size() > 0) {
            messages.remove(index);
        }
        super.conditions.remove(property);
    }

    /**
     * Checks the messages list and if the corresponding boolean condition is false
     * adds the message to the {@code StringBuilder}.
     */
    public String getMessages() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            if (!conditions.get(i).get()) {
                sb.append(messages.get(i)).append(",\n");
            }
        }
        return StringUtils.replaceLast(sb.toString(), ",", ".");
    }

    public DialogType getDialogType() {
        return dialogType.get();
    }

    public ObjectProperty<DialogType> dialogTypeProperty() {
        return dialogType;
    }

    public void setDialogType(DialogType dialogType) {
        this.dialogType.set(dialogType);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}