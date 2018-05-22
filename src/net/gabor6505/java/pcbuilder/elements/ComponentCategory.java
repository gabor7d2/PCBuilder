package net.gabor6505.java.pcbuilder.elements;

import net.gabor6505.java.pcbuilder.components.Component;
import net.gabor6505.java.pcbuilder.components.GenericComponent;
import net.gabor6505.java.pcbuilder.utils.Utils;
import net.gabor6505.java.pcbuilder.xml.XmlContract;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class ComponentCategory {

    private final static Dimension defaultPanelSize = new Dimension(128, 180);

    private final String name;
    private final String displayName;

    private final CategoryPreview preview;
    private final List<CategoryItem> items = new ArrayList<>();

    private final ButtonGroup buttonGroup = new ButtonGroup(true);

    public ComponentCategory(String categoryName, String displayName, List components, Dimension previewPanelSize, Dimension itemSize) {
        name = categoryName;
        this.displayName = displayName;
        preview = new CategoryPreview(displayName, previewPanelSize);
        if (components == null) components = new ArrayList();
        for (Object obj : components) {
            Component componentInfo = (Component) obj;
            items.add(new CategoryItem(componentInfo, buttonGroup, itemSize));
        }
    }

    public ComponentCategory(String categoryName, String displayName, List components, Dimension panelsSize) {
        this(categoryName, displayName, components, panelsSize, panelsSize);
    }

    public ComponentCategory(String categoryName, String displayName, List components) {
        this(categoryName, displayName, components, defaultPanelSize, defaultPanelSize);
    }

    public ComponentCategory(String categoryName, List components, Dimension panelsSize) {
        this(categoryName, categoryName, components, panelsSize, panelsSize);
    }

    public ComponentCategory(String categoryName, List components) {
        this(categoryName, categoryName, components, defaultPanelSize, defaultPanelSize);
    }

    public ComponentCategory(String name) {
        this(name, GenericComponent.getComponents(name));
    }

    public ComponentCategory(String categoryName, String displayName) {
        this(categoryName, displayName, GenericComponent.getComponents(categoryName));
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

    public List<JPanel> getItemPanels() {
        return new ArrayList<>(items);
    }

    public List<JComponent> getItemComponents() {
        return new ArrayList<>(items);
    }

    public class CategoryPreview extends JPanel {

        public CategoryPreview(String categoryName, Dimension size) {
            setLayout(new BorderLayout());
            Utils.fixSize(this, size);

            setBorder(BorderFactory.createMatteBorder(32, 12, 32, 12, getBackground()));

            String filePath = XmlContract.Folder.CATEGORY_IMAGES.getValue() + categoryName.replace(' ', '_') + ".png";
            ImageLabel imageLabel = new ImageLabel(filePath, 80, 80, this);
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel nameLabel = new JLabel(categoryName);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 14));
            add(nameLabel, BorderLayout.SOUTH);
        }
    }

    public class CategoryItem extends JPanel implements MouseListener {

        private final Component componentInfo;

        public CategoryItem(Component componentInfo, ButtonGroup group, Dimension size) {
            this.componentInfo = componentInfo;

            Utils.fixSize(this, size);
            addMouseListener(this);

            //setBorder(BorderFactory.createLineBorder(Color.WHITE, 8));
            //setBackground(Utils.averageColor(((JLabel) preview.getComponent(0)).getIcon(), 0, 0, 80, 80));
            setBackground(Color.WHITE);

            JRadioButton radioButton = new JRadioButton();
            radioButton.setHorizontalAlignment(SwingConstants.CENTER);
            radioButton.setBackground(Color.WHITE);
            group.add(radioButton);
            add(radioButton);

            ImageLabel image = new ImageLabel(componentInfo.getImagePath(), 88, 88, this);
            image.setBackground(Color.WHITE);
            image.setBorder(Color.WHITE, 8);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(Color.WHITE);
            add(textPanel);

            JLabel brand = new JLabel(componentInfo.getBrandName());
            brand.setFont(new Font(brand.getFont().getName(), Font.BOLD, 13));
            brand.setAlignmentX(CENTER_ALIGNMENT);
            brand.setBackground(Color.WHITE);
            textPanel.add(brand);

            JLabel modelNumber = new JLabel(componentInfo.getModelNumber());
            modelNumber.setFont(new Font(modelNumber.getFont().getName(), Font.PLAIN, 13));
            modelNumber.setAlignmentX(CENTER_ALIGNMENT);
            modelNumber.setBackground(Color.WHITE);
            textPanel.add(modelNumber);

            boolean firstInfo = true;
            for (String info : componentInfo.getExtraInfo()) {
                JLabel extraInfo = new JLabel(info);
                extraInfo.setFont(new Font(extraInfo.getFont().getName(), Font.PLAIN, 11));
                extraInfo.setAlignmentX(CENTER_ALIGNMENT);
                extraInfo.setBackground(Color.WHITE);
                textPanel.add(extraInfo);

                if (firstInfo) {
                    firstInfo = false;
                    extraInfo.setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0, Color.WHITE));
                }
                Utils.adjustHeight(this, extraInfo);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //((AbstractButton)((JPanel) e.getComponent()).getComponent(0)).setSelected(true);
            Utils.openWebsite(componentInfo.getProductSite());
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
}
