/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * SelBot.java requires one additional file:
 *   images/middle.gif.
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class SelBot extends JPanel {
    public SelBot() throws IOException, SQLException {
        super(new GridLayout(1, 1));

        UIManager.put("TabbedPane.focus", Color.RED);
        JTabbedPane tabbedPane = new JTabbedPane(){
            public Color getForegroundAt(int index){
                if(getSelectedIndex() == index) return Color.BLACK;
                return Color.BLACK;
            }
            public Color getBackgroundAt(int index){
                if(getSelectedIndex() == index) return new Color(245, 252, 255);
                return new Color(151,193,216);
            }
        };
        //System.out.println(new File("./images/dot.png").getCanonicalPath());
        //ImageIcon icon = createImageIcon("");
        ImageIcon pageicon = new ImageIcon("./images/page.png");
        ImageIcon elementicon = new ImageIcon("./images/cursor.png");
        ImageIcon testicon = new ImageIcon("./images/test.png");
        ImageIcon gearicon = new ImageIcon("./images/gear.png");

        Color titleColor = new Color(151,193,216);
        Color bgColor = new Color(245, 252, 255);
        Border border = BorderFactory.createLineBorder(new Color(60,90,128),1);
        UIManager UI=new UIManager();
        UI.put("OptionPane.background",new ColorUIResource(245,252,255));
        UI.put("Panel.background",new ColorUIResource(245,252,255));

        final Connection conn = MySqlConnection.getConnection();

        final Vector comboBoxItems=new Vector();
        ResultSet rslist = MySqlConnection.getPages(conn);
        while(rslist.next())
        {
            String pageName = rslist.getString("pageName");
            comboBoxItems.add(pageName);
        }

        final Vector comboBoxItems3=new Vector();
        ResultSet testlist = MySqlConnection.getTestList(conn);
        while(testlist.next())
        {
            String test = testlist.getString("testName");
            comboBoxItems3.add(test);
        }

        /**
         * Panel 1
         */
        String data[][]={};
        String column[]={"ID","NAME","DELETE"};
        final DefaultTableModel model = new DefaultTableModel(data,column);

        JComponent panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
        panel1.setPreferredSize(new Dimension(700, 600));
        JPanel titlePanel1 = new JPanel();
        titlePanel1.setBackground(titleColor);
        titlePanel1.setMaximumSize(new Dimension(700,40));
        titlePanel1.setBorder(border);
        JLabel title1 = new JLabel("Manage Pages");
        title1.setFont(new Font("Adobe Clean", Font.BOLD, 20));
        title1.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel1.add(title1);
        panel1.add(titlePanel1);
        panel1.setBackground(bgColor);
        panel1.setBorder(border);

        JLabel pagelabel = new JLabel("Enter Page Name");
        pagelabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JTextField newpage = new JTextField("",20);
        newpage.setMaximumSize(new Dimension(200, 20));
        newpage.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton addbutton = new JButton("Add Page");
        addbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel1.add(Box.createRigidArea(new Dimension(5, 40)));
        panel1.add(pagelabel);
        panel1.add(Box.createRigidArea(new Dimension(5, 5)));
        panel1.add(newpage);
        panel1.add(Box.createRigidArea(new Dimension(5, 10)));
        panel1.add(addbutton);
        panel1.add(Box.createRigidArea(new Dimension(5, 20)));
        JComponent subpanel1 = new JPanel();
        subpanel1.setBackground(bgColor);
        JTable jt=new JTable(model){
            public boolean isCellEditable(int row,int column){
                if(column == 1 || column == 0) return false;//the 4th column is not editable
                return true;
            }
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
            {
                Component c = super.prepareRenderer(renderer, row, column);

                //  Color row based on a cell value

                if (isRowSelected(row)){ //When A row is selected
                    c.setBackground(getBackground());//Set Background
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        };
        jt.setGridColor(Color.LIGHT_GRAY);
        jt.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        jt.setIntercellSpacing(new Dimension(10,10));

        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                int pageId = (Integer) table.getModel().getValueAt(modelRow, 0);
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);
                MySqlConnection.deleteFromPages(conn, pageId);
                comboBoxItems.remove(modelRow);
            }
        };

        SetRowHeight(jt);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jt.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jt.getColumnModel().getColumn(0).setHeaderRenderer(centerRenderer);
        jt.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jt.getColumnModel().getColumn(1).setHeaderRenderer(centerRenderer);
        ButtonColumn buttonColumn = new ButtonColumn(jt, delete, 2);
        buttonColumn.setMnemonic(KeyEvent.VK_D);
        //jt.getColumnModel().getColumn(2).setCellRenderer(new ButtonColumn(jt,delete,2));
        jt.getColumnModel().getColumn(2).setHeaderRenderer(centerRenderer);
        jt.setBounds(30,40,120,200);
        JScrollPane sp=new JScrollPane(jt);
        sp.setPreferredSize(new Dimension(500,300));
        subpanel1.add(sp,BorderLayout.CENTER);
        panel1.add(subpanel1);
        panel1.add(Box.createRigidArea(new Dimension(5, 50)));

        ResultSet rs = MySqlConnection.getPages(conn);
        DefaultTableModel temp = (DefaultTableModel) jt.getModel();
        while(rs.next())
        {
            int id = rs.getInt("pageId");
            String pageName = rs.getString("pageName");
            //Object[][]data={{n,e}};
            // This will add row from the DB as the last row in the JTable.
            model.insertRow(jt.getRowCount(), new Object[] {id, pageName,"Delete"});
        }


        /**
         * Panel 2
         */
        String data2[][]={};
        String column2[]={"ID","NAME","TYPE","METHOD","LOCATOR","PAGEID","DELETE"};
        final DefaultTableModel model2 = new DefaultTableModel(data2,column2);
        final Connection conn2 = MySqlConnection.getConnection();

        JComponent panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
        panel2.setPreferredSize(new Dimension(700, 600));
        JPanel titlePanel2 = new JPanel();
        titlePanel2.setBackground(titleColor);
        titlePanel2.setMaximumSize(new Dimension(700,40));
        titlePanel2.setBorder(border);
        JLabel title2 = new JLabel("Manage Elements");
        title2.setFont(new Font("Adobe Clean", Font.BOLD, 20));
        title2.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel2.add(title2);
        panel2.add(titlePanel2);
        panel2.setBackground(bgColor);
        panel2.setBorder(border);

        final DefaultComboBoxModel cbmodel = new DefaultComboBoxModel(comboBoxItems);
        final JComboBox comboBox = new JComboBox(cbmodel);
        comboBox.setMaximumSize(new Dimension(200, 20));
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel comboBoxLabel = new JLabel("Select Page");
        comboBoxLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel elementNameLabel = new JLabel("Element Name & Type");
        elementNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JTextField elementName = new JTextField("",20);
        elementName.setMaximumSize(new Dimension(200, 20));
        elementName.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JComboBox comboBoxType = new JComboBox(new String[]{"TextBox", "Button","Link","PlainText"});
        comboBoxType.setMaximumSize(new Dimension(200, 20));
        comboBoxType.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel elementLocatorLabel = new JLabel("Element Locator");
        elementLocatorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JComboBox comboBoxMethod = new JComboBox(new String[]{"id", "name","xpath", "css", "linkText", "className"});
        comboBoxMethod.setMaximumSize(new Dimension(200, 20));
        comboBoxMethod.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JTextField elementLocator = new JTextField("",20);
        elementLocator.setMaximumSize(new Dimension(200, 20));
        elementLocator.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton addbutton2 = new JButton("Add Element");
        addbutton2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel subpanelelement = new JPanel(new FlowLayout());
        JPanel subpanellocator = new JPanel(new FlowLayout());
        subpanelelement.setBackground(bgColor);
        subpanellocator.setBackground(bgColor);

        panel2.add(Box.createRigidArea(new Dimension(5, 10)));
        panel2.add(comboBoxLabel);
        panel2.add(Box.createRigidArea(new Dimension(5, 5)));
        panel2.add(comboBox);
        panel2.add(Box.createRigidArea(new Dimension(5, 10)));
        subpanelelement.add(elementNameLabel);
        subpanelelement.add(Box.createRigidArea(new Dimension(5, 5)));
        subpanelelement.add(elementName);
        subpanelelement.add(Box.createRigidArea(new Dimension(5, 5)));
        subpanelelement.add(comboBoxType);

        subpanellocator.add(elementLocatorLabel);
        subpanellocator.add(Box.createRigidArea(new Dimension(5, 5)));
        subpanellocator.add(comboBoxMethod);
        subpanellocator.add(Box.createRigidArea(new Dimension(5, 5)));
        subpanellocator.add(elementLocator);
        panel2.add(subpanelelement);
        panel2.add(Box.createRigidArea(new Dimension(5, 10)));
        panel2.add(subpanellocator);
        panel2.add(Box.createRigidArea(new Dimension(5, 10)));
        panel2.add(addbutton2);
        panel2.add(Box.createRigidArea(new Dimension(5, 15)));
        JComponent subpanel2 = new JPanel();
        subpanel2.setBackground(bgColor);
        JTable jt2=new JTable(model2){
            public boolean isCellEditable(int row,int column){
                if(column != 6) return false;//the 4th column is not editable
                return true;
            }
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
            {
                Component c = super.prepareRenderer(renderer, row, column);

                //  Color row based on a cell value

                if (isRowSelected(row)){ //When A row is selected
                    c.setBackground(getBackground());//Set Background
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        };
        jt2.setGridColor(Color.LIGHT_GRAY);
        jt2.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        jt2.setIntercellSpacing(new Dimension(10,10));

        Action delete2 = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                int elementId = (Integer) table.getModel().getValueAt(modelRow, 0);
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);
                MySqlConnection.deleteFromElements(conn2, elementId);
                MySqlConnection.deleteFromFunctions(conn2, elementId);
            }
        };

        SetRowHeight(jt2);
        jt2.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(0).setHeaderRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(0).setMaxWidth(40);
        jt2.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(1).setHeaderRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(2).setHeaderRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(3).setHeaderRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(4).setHeaderRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(5).setHeaderRenderer(centerRenderer);
        jt2.getColumnModel().getColumn(5).setMaxWidth(50);
        ButtonColumn buttonColumn2 = new ButtonColumn(jt2, delete2, 6);
        buttonColumn2.setMnemonic(KeyEvent.VK_D);
        //jt.getColumnModel().getColumn(2).setCellRenderer(new ButtonColumn(jt,delete,2));
        jt2.getColumnModel().getColumn(6).setHeaderRenderer(centerRenderer);
        JScrollPane sp2=new JScrollPane(jt2);
        sp2.setPreferredSize(new Dimension(650,300));
        subpanel2.add(sp2,BorderLayout.CENTER);
        panel2.add(subpanel2);
        panel2.add(Box.createRigidArea(new Dimension(5, 50)));

        ResultSet rs2 = MySqlConnection.getElements(conn2);
        DefaultTableModel temp2 = (DefaultTableModel) jt2.getModel();
        while(rs2.next())
        {
            int id = rs2.getInt("elementId");
            String name = rs2.getString("elementName");
            String type = rs2.getString("elementType");
            String method = rs2.getString("method");
            String locator = rs2.getString("locator");
            String pageId = rs2.getString("pageId");
            //Object[][]data={{n,e}};
            // This will add row from the DB as the last row in the JTable.
            model2.insertRow(jt2.getRowCount(), new Object[] {id, name, type, method, locator, pageId, "Delete"});
        }

        addbutton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nextId = MySqlConnection.getNextIdElements(conn2);
                int nextFunctionId = MySqlConnection.getNextIdFunctions(conn2);
                int pageId = MySqlConnection.getPageIdForPageName(conn2, comboBox.getSelectedItem().toString());
                model2.addRow(new Object[]{nextId,elementName.getText(),comboBoxType.getSelectedItem().toString(),comboBoxMethod.getSelectedItem().toString(), elementLocator.getText(), pageId, "Delete"});
                MySqlConnection.insertIntoElements(conn2,nextId,elementName.getText(),comboBoxType.getSelectedItem().toString(),comboBoxMethod.getSelectedItem().toString(), elementLocator.getText(), pageId);
                String str = elementName.getText();
                str = str.substring(0, 1).toUpperCase() + str.substring(1);
                String fxn = str;
                String type = comboBoxType.getSelectedItem().toString();
                if(type.equals("TextBox")){
                    fxn = "typeIn" + str;
                }
                else if(type.equals("Button") || type.equals("Link")){
                    fxn = "click" + str;
                } else if(type.equals("PlainText")){
                    fxn = "validate" + str;
                }
                MySqlConnection.insertIntoFunctions(conn2, nextFunctionId, fxn, nextId, pageId);
                if(type.equals("TextBox")){
                    fxn = "submit" + str;
                    MySqlConnection.insertIntoFunctions(conn2, nextFunctionId+1, fxn, nextId, pageId);
                }
                if(type.equals("Link")){
                    fxn = "validate" + str;
                    MySqlConnection.insertIntoFunctions(conn2, nextFunctionId+1, fxn, nextId, pageId);
                }

                elementName.setText("");
                elementLocator.setText("");
            }
        });

        //dependency
        addbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nextId = MySqlConnection.getNextIdPages(conn);
                model.addRow(new Object[]{nextId,newpage.getText(),"Delete"});
                MySqlConnection.insertIntoPages(conn,nextId,newpage.getText());
                comboBoxItems.add(newpage.getText());
                newpage.setText("");
            }
        });

        /**
         * Panel 3
         */
        String data3[][]={};
        String column3[]={"ID","STEP DESCRIPTION","FUNCTION","DELETE"};
        final DefaultTableModel model3 = new DefaultTableModel(data3,column3);
        final Connection conn3 = MySqlConnection.getConnection();

        JComponent panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));
        panel3.setPreferredSize(new Dimension(700, 600));
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(titleColor);
        titlePanel.setMaximumSize(new Dimension(700,40));
        titlePanel.setBorder(border);
        JLabel title3 = new JLabel("Manage Tests");
        title3.setFont(new Font("Adobe Clean", Font.BOLD, 20));
        title3.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(title3);
        panel3.add(titlePanel);
        panel3.setBackground(bgColor);
        panel3.setBorder(border);

        final DefaultComboBoxModel cbmodel3 = new DefaultComboBoxModel(comboBoxItems3);
        final JComboBox comboBox3 = new JComboBox(cbmodel3);
        comboBox3.setMaximumSize(new Dimension(200, 30));
        comboBox3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel comboBoxLabel3 = new JLabel("Select Test");
        comboBoxLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton newTestButton = new JButton("OR Add New Test");
        newTestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String m = JOptionPane.showInputDialog(null,"Enter Test Name:","Add New Test",JOptionPane.INFORMATION_MESSAGE);
                System.out.println(m);
                if(m!=null) {
                    comboBoxItems3.add(m);
                    comboBox3.setSelectedItem(m);
                }
            }
        });
        JLabel testStepLabel = new JLabel("Enter Test Step");
        testStepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JTextField testStep = new JTextField("",20);
        testStep.setMaximumSize(new Dimension(500, 20));
        testStep.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton generateButton = new JButton("Generate");
        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel resultPanel = new JPanel(new FlowLayout());
        JLabel functionLabel = new JLabel("Generated Function:");
        functionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JLabel function = new JLabel("Null");
        function.setFont(new Font("", Font.BOLD + Font.ITALIC, 14));
        function.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(functionLabel);
        resultPanel.add(function);
        resultPanel.setBackground(bgColor);

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setMaximumSize(new Dimension(500, 20));
        actionPanel.setBackground(bgColor);
        JButton launchButton = new JButton("Launch Browser");
        launchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JButton testButton = new JButton("Test Function");
        testButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton addButton = new JButton("Add Function");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nextId = MySqlConnection.getNextIdTests(conn3);
                model3.addRow(new Object[]{nextId, testStep.getText(), function.getText(),"Delete"});
                MySqlConnection.insertIntoTests(conn,nextId, testStep.getText(), function.getText(),comboBox3.getSelectedItem().toString());
                testStep.setText("");
                function.setText("");
            }
        });;

        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Launch.launchBrowser();
                testButton.setEnabled(true);
            }
        });

        testButton.setEnabled(false);
        final String[] prev = {""};
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String ftext = function.getText().toLowerCase();
                    if(prev[0].contains("typeIn") && (ftext.contains("enter") && ftext.contains("press")) || ftext.contains("hit") || ftext.contains("submit")){
                        String sub = prev[0].substring(0,prev[0].indexOf('(')).replace("typeIn","submit")+"()";
                        TestOpenBrowser.runStep( sub );
                    } else if(function.getText().contains("typeIn")){
                        prev[0] = function.getText();
                        TestOpenBrowser.runStep(function.getText());
                    } else
                        TestOpenBrowser.runStep(function.getText());
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }
        });

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fxn= "";
                String ftext = testStep.getText().toLowerCase();
                try {
                    if(prev[0].contains("typeIn") && (ftext.contains("enter") && ftext.contains("press")) || ftext.contains("hit")){
                        fxn = prev[0].substring(0,prev[0].indexOf('(')).replace("typeIn","submit")+"()";
                    } else
                        fxn = BreakInput.getFunction(testStep.getText());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                function.setText(fxn);
            }
        });

        actionPanel.add(launchButton);
        actionPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        actionPanel.add(testButton);
        actionPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        actionPanel.add(addButton);

        panel3.add(Box.createRigidArea(new Dimension(5, 10)));
        panel3.add(comboBoxLabel3);
        panel3.add(Box.createRigidArea(new Dimension(5, 10)));
        panel3.add(comboBox3);
        panel3.add(Box.createRigidArea(new Dimension(5, 10)));
        panel3.add(newTestButton);
        panel3.add(Box.createRigidArea(new Dimension(5, 20)));
        panel3.add(testStepLabel);
        panel3.add(Box.createRigidArea(new Dimension(5, 10)));
        panel3.add(testStep);
        panel3.add(Box.createRigidArea(new Dimension(5, 10)));
        panel3.add(generateButton);
        panel3.add(Box.createRigidArea(new Dimension(5, 10)));
        panel3.add(resultPanel);
        panel3.add(Box.createRigidArea(new Dimension(5, 10)));
        panel3.add(actionPanel);

        JComponent subpanel3 = new JPanel();
        final JTable jt3=new JTable(model3){
            public boolean isCellEditable(int row,int column){
                if(column == 1 || column == 0 || column == 2) return false;//the 4th column is not editable
                return true;
            }
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
            {
                Component c = super.prepareRenderer(renderer, row, column);

                //  Color row based on a cell value

                if (isRowSelected(row)){ //When A row is selected
                    c.setBackground(getBackground());//Set Background
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        };
        jt3.setGridColor(Color.LIGHT_GRAY);
        jt3.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        jt3.setIntercellSpacing(new Dimension(10,10));

        Action delete3 = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                int stepId = (Integer) table.getModel().getValueAt(modelRow, 0);
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);
                MySqlConnection.deleteFromTests(conn2, stepId);
            }
        };

        SetRowHeight(jt3);
        jt3.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jt3.getColumnModel().getColumn(0).setMaxWidth(100);
        jt3.getColumnModel().getColumn(0).setHeaderRenderer(centerRenderer);
        jt3.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jt3.getColumnModel().getColumn(1).setHeaderRenderer(centerRenderer);
        jt3.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jt3.getColumnModel().getColumn(2).setHeaderRenderer(centerRenderer);
        ButtonColumn buttonColumn3 = new ButtonColumn(jt3, delete3, 3);
        buttonColumn3.setMnemonic(KeyEvent.VK_D);
        //jt.getColumnModel().getColumn(2).setCellRenderer(new ButtonColumn(jt,delete,2));
        jt3.getColumnModel().getColumn(3).setMaxWidth(150);
        jt3.getColumnModel().getColumn(3).setHeaderRenderer(centerRenderer);
        JScrollPane sp3=new JScrollPane(jt3);
        sp3.setPreferredSize(new Dimension(650,200));
        sp3.setBackground(bgColor);
        subpanel3.add(sp3,BorderLayout.CENTER);
        subpanel3.setBackground(bgColor);
        panel3.add(subpanel3);
        panel3.add(Box.createRigidArea(new Dimension(5, 50)));

        if(comboBox3.getSelectedItem()!=null) {
            String testName = comboBox3.getSelectedItem().toString();
            ResultSet rs3 = MySqlConnection.getTestSteps(conn3, testName);
            while (rs3.next()) {
                int id = rs3.getInt("stepId");
                String description = rs3.getString("stepDescription");
                String fxn = rs3.getString("function");
                model3.insertRow(jt3.getRowCount(), new Object[]{id, description, fxn, "Delete"});
            }
        }
        comboBox3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model3.getRowCount() > 0) {
                    for (int i = model3.getRowCount() - 1; i > -1; i--) {
                        model3.removeRow(i);
                    }
                }
                String testName = comboBox3.getSelectedItem().toString();
                ResultSet rs3 = MySqlConnection.getTestSteps(conn3, testName);
                try {
                    while (rs3.next()) {
                        int id = rs3.getInt("stepId");
                        String description = rs3.getString("stepDescription");
                        String fxn = rs3.getString("function");
                        model3.insertRow(jt3.getRowCount(), new Object[]{id, description, fxn, "Delete"});
                    }
                } catch (Exception e1){
                    System.out.println("Issue while repopulating tests table");
                }
            }
        });

        JComponent panel4 = new JPanel();
        panel4.setLayout(new BoxLayout(panel4, BoxLayout.PAGE_AXIS));
        panel4.setPreferredSize(new Dimension(700, 600));
        JPanel titlePanel4 = new JPanel();
        titlePanel4.setBackground(titleColor);
        titlePanel4.setMaximumSize(new Dimension(700,40));
        JLabel title4 = new JLabel("Generate Suite");
        title4.setFont(new Font("Adobe Clean", Font.BOLD, 20));
        title4.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel4.add(title4);
        titlePanel4.setBorder(border);
        panel4.add(titlePanel4);
        panel4.setOpaque(true);
        panel4.setBackground(bgColor);
        panel4.setBorder(border);

        JLabel pagelabel4 = new JLabel("Enter/Browse Project Location");
        pagelabel4.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel loc = new JPanel(new FlowLayout());
        final JTextField locationField = new JTextField("",20);
        JButton browse = new JButton("Browse");
        loc.add(locationField);
        loc.add(browse);
        loc.setAlignmentX(Component.CENTER_ALIGNMENT);
        loc.setMaximumSize(new Dimension(400, 50));
        loc.setBackground(bgColor);
        JButton generateSuite = new JButton("Generate Suite");
        generateSuite.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel4.add(Box.createRigidArea(new Dimension(5, 40)));
        panel4.add(pagelabel4);
        panel4.add(Box.createRigidArea(new Dimension(5, 15)));
        panel4.add(loc);
        panel4.add(Box.createRigidArea(new Dimension(5, 15)));
        panel4.add(generateSuite);

        generateSuite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    GenerateCode.generateCode(locationField.getText());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                JOptionPane.showMessageDialog(null, "The Test Suite has been generated!");
            }
        });

        tabbedPane.addTab("Manage Pages", pageicon, panel1,
                "Create and remove pages");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        tabbedPane.addTab("Manage Elements", elementicon, panel2,
                "Add new elements");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        tabbedPane.addTab("Manage Tests", testicon, panel3,
                "Add test steps");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        tabbedPane.addTab("Generate Suite", gearicon, panel4,
                "Generate automation test suite");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

        //Add the tabbed pane to this panel.
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = SelBot.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI() throws IOException, SQLException {
        //Create and set up the window.
        JFrame frame = new JFrame("Selenium Automation Toolkit");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(new Color(151,193,216));

        //Add content to the window.
        frame.add(new SelBot(), BorderLayout.CENTER);
        frame.setIconImage(new ImageIcon("./images/bot.png").getImage());

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void SetRowHeight(JTable table){
        int height = table.getRowHeight();
        table.setRowHeight(height+10);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                //UIManager.put("swing.boldMetal", Boolean.FALSE);
                try {
                    createAndShowGUI();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

}