package net.gabor6505.java.pcbuilder.elements;

import net.gabor6505.java.pcbuilder.components.Component;
import net.gabor6505.java.pcbuilder.gui.ProfileManager;
import net.gabor6505.java.pcbuilder.utils.Utils;
import net.gabor6505.java.pcbuilder.xml.XmlContract;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class ComponentCategory {

    private final static Dimension defaultPanelSize = new Dimension(128, 188);

    private static ImagePreviewDialog previewer;

    private static ImageLabel previousImageLabel;

    private final String name;
    private final String displayName;

    private final String url;

    private final CategoryPreview preview;
    private final List<CategoryItem> items = new ArrayList<>();

    private final ButtonGroup buttonGroup = new ButtonGroup(true);

    private final List<CategoryItemListener> listeners = new ArrayList<>();

    public ComponentCategory(String categoryName, String displayName, List components, Dimension previewPanelSize, Dimension itemSize, String categoryUrl, int selectedIndex) {
        url = categoryUrl;
        name = categoryName;
        this.displayName = displayName;

        preview = new CategoryPreview(previewPanelSize);

        if (components == null) components = new ArrayList();
        for (Object obj : components) {
            Component componentInfo = (Component) obj;
            items.add(new CategoryItem(componentInfo, itemSize));
        }

        buttonGroup.addButtonGroupListener((button, index) -> {
            for (CategoryItemListener l : listeners) {
                l.itemSelected(this, items.get(index), index);
            }
        });
        buttonGroup.setSelectedIndex(selectedIndex);
    }

    public ComponentCategory(String categoryName, String displayName, List components, String categoryUrl, int selectedIndex) {
        this(categoryName, displayName, components, defaultPanelSize, defaultPanelSize, categoryUrl, selectedIndex);
    }

    public void addCategoryItemListener(CategoryItemListener listener) {
        listeners.add(listener);
    }

    public void removeCategoryItemListener(CategoryItemListener listener) {
        listeners.remove(listener);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public CategoryPreview getPreview() {
        return preview;
    }

    public JPanel getPreviewPanel() {
        return preview;
    }

    public List<CategoryItem> getItems() {
        return items;
    }

    /**
     * @return The selected item in this category, or null if there aren't any items in this category
     */
    public CategoryItem getSelectedItem() {
        int index = buttonGroup.getSelectedIndex();
        if (index < 0) return null;
        return items.get(index);
    }

    public List<JPanel> getItemPanels() {
        return new ArrayList<>(items);
    }

    public List<JComponent> getItemComponents() {
        return new ArrayList<>(items);
    }

    public class CategoryPreview extends JPanel implements MouseListener {

        private final ImageLabel imageLabel;

        public CategoryPreview(Dimension size) {
            setLayout(new BorderLayout());
            Utils.fixSize(this, size);

            setBorder(BorderFactory.createMatteBorder(32, 12, 32, 12, getBackground()));

            String filePath = XmlContract.Folder.CATEGORY_IMAGES.getValue() + ComponentCategory.this.name.replace(' ', '_') + ".png";
            imageLabel = new ImageLabel(filePath, 80, 80, this, false);
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.addMouseListener(this);

            JLabel nameLabel = new JLabel(ComponentCategory.this.displayName);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14));
            add(nameLabel, BorderLayout.SOUTH);
        }

        public ImageLabel getImageLabel() {
            return imageLabel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (url != null && !url.isEmpty()) {
                Utils.openWebsite(url);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public class CategoryItem extends JPanel implements MouseListener, KeyListener {

        private final ImageLabel imageLabel;
        private final Component component;

        private int previewCompIndex = -1;

        public CategoryItem(Component component, Dimension size) {
            this.component = component;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            Utils.fixSize(this, size);
            setName("panel");
            addMouseListener(this);

            //setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
            //setBackground(Utils.averageColor(((JLabel) preview.getComponent(0)).getIcon(), 0, 0, 80, 80));
            setBackground(Color.WHITE);
            setAlignmentY(TOP_ALIGNMENT);

            JRadioButton radioButton = new JRadioButton();
            radioButton.setBorder(BorderFactory.createMatteBorder(8, 0, 8, 0, Color.WHITE));
            radioButton.setAlignmentX(CENTER_ALIGNMENT);
            radioButton.setBackground(Color.WHITE);
            ComponentCategory.this.buttonGroup.add(radioButton);
            add(radioButton);

            if (previousImageLabel != null && previousImageLabel.getImagePath().equals(component.getImagePath())) {
                imageLabel = new ImageLabel(component.getImagePath(), previousImageLabel, 104, 104, this);
            } else {
                imageLabel = new ImageLabel(component.getImagePath(), 104, 104, this);
            }
            imageLabel.setBorder(Color.WHITE, 4);
            imageLabel.setBackground(Color.WHITE);
            imageLabel.setName("image");
            imageLabel.addMouseListener(this);
            imageLabel.setAlignmentX(CENTER_ALIGNMENT);
            previousImageLabel = imageLabel;

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 4, Color.WHITE));
            textPanel.setBackground(Color.WHITE);
            textPanel.setAlignmentX(CENTER_ALIGNMENT);
            add(textPanel);

            SelectableLabel brand = new SelectableLabel(component.getBrandName(), "brand", this).centerText();
            brand.setFont(brand.getFont().deriveFont(Font.BOLD, 13));
            textPanel.add(brand);

            SelectableLabel modelNumber = new SelectableLabel(component.getModelNumber(), "modelNumber", this).centerText();
            modelNumber.setFont(modelNumber.getFont().deriveFont(Font.PLAIN, 13));
            textPanel.add(modelNumber);

            if (component.getPrice() != null && !component.getPrice().isEmpty()) {
                SelectableLabel price = new SelectableLabel(component.getFormattedPrice(), "price", this).centerText();
                price.setFont(price.getFont().deriveFont(Font.PLAIN, 12));
                textPanel.add(price);
                Utils.adjustHeight(this, price);
            }

            boolean firstInfo = true;
            for (String info : component.getExtraInfo()) {
                SelectableLabel extraInfo = new SelectableLabel(info).centerText();
                extraInfo.setFont(extraInfo.getFont().deriveFont(Font.PLAIN, 11));
                textPanel.add(extraInfo);

                if (firstInfo) {
                    firstInfo = false;
                    extraInfo.setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0, Color.WHITE));
                }
                Utils.adjustHeight(this, extraInfo);
            }

            // Make the panel larger if the text doesn't fit
            if (textPanel.getPreferredSize().width > getWidth()) {
                Utils.fixSize(this, textPanel.getPreferredSize().width, getHeight());
            }
        }

        public ImageLabel getImageLabel() {
            return imageLabel;
        }

        public Component getComponent() {
            return component;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            java.awt.Component comp = e.getComponent();

            switch (comp.getName()) {
                case "image":
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        Utils.openWebsite(component.getPriceSite());
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        showComponentPreview(component);
                    }
                    break;
                case "brand":
                case "modelNumber":
                    Utils.openWebsite(component.getProductSite());
                    break;
                case "price":
                    Utils.openWebsite(component.getShopSite());
                    break;
                case "panel":
                    //((AbstractButton)((JPanel) e.getComponent()).getComponent(0)).setSelected(true);
                    Utils.openWebsite(component.getPriceSite());
                    break;
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                // Show the previous component's preview
                case KeyEvent.VK_B:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_KP_LEFT:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_KP_UP:
                case KeyEvent.VK_PAGE_UP:
                    if (previewCompIndex > 0) {
                        previewCompIndex--;
                        Component currentComp = getItems().get(previewCompIndex).getComponent();
                        String title = "Preview: " + currentComp.getBrandName() + " " + currentComp.getModelNumber();
                        previewer.updateImage(currentComp.getImagePath());
                        previewer.setTitle(title);
                    }
                    break;
                // Show the next component's preview
                case KeyEvent.VK_N:
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_KP_RIGHT:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_KP_DOWN:
                case KeyEvent.VK_PAGE_DOWN:
                    if (previewCompIndex < getItems().size() - 1 && previewCompIndex > -1) {
                        previewCompIndex++;
                        Component currentComp = getItems().get(previewCompIndex).getComponent();
                        String title = "Preview: " + currentComp.getBrandName() + " " + currentComp.getModelNumber();
                        previewer.updateImage(currentComp.getImagePath());
                        previewer.setTitle(title);
                    }
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        private void showComponentPreview(Component comp) {
            previewCompIndex = getItems().indexOf(this);
            String title = "Preview: " + comp.getBrandName() + " " + comp.getModelNumber();
            previewer = new ImagePreviewDialog(comp.getImagePath(), title, 512, 512);
            previewer.addKeyListener(this);
            previewer.getImageLabel().addKeyListener(this);
            ProfileManager.getInstance().incrementOpenDialogCount();
            previewer.setVisible(true);
            ProfileManager.getInstance().decrementOpenDialogCount();
        }
    }

    public interface CategoryItemListener {
        void itemSelected(ComponentCategory category, CategoryItem item, int index);
    }
}
