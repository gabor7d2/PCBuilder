package net.gabor6505.java.pcbuilder.gui.dialog;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class ProgressDialogType {

    public final static ProgressDialogType INDETERMINATE = new ProgressDialogType(DO_NOTHING_ON_CLOSE, true,true, null);
    public final static ProgressDialogType DETERMINATE = new ProgressDialogType(DO_NOTHING_ON_CLOSE, false, true, null);
    public final static ProgressDialogType STARTUP = new ProgressDialogType(DO_NOTHING_ON_CLOSE, true, true, (e, dialog) -> System.exit(0));

    private final int closeOperation;
    private final boolean indeterminate;
    private final boolean modal;
    private final ProgressDialog.DialogClosingListener listener;

    public ProgressDialogType(int closeOperation, boolean indeterminate, boolean modal, ProgressDialog.DialogClosingListener closingListener) {
        this.closeOperation = closeOperation;
        this.indeterminate = indeterminate;
        this.modal = modal;
        this.listener = closingListener;
    }

    public int getCloseOperation() {
        return closeOperation;
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    public boolean isModal() {
        return modal;
    }

    public ProgressDialog.DialogClosingListener getListener() {
        return listener;
    }
}
