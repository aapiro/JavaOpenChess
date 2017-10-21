/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.art.lach.mateusz.javaopenchess.network;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;

/**
 * Class representing the game chat
 * Players are in touch and can write a messages to each other
 * @author Mateusz  Lach ( matlak, msl )
 * @author Damian Marciniak
 */
public class Chat extends JPanel implements ActionListener
{
    private static final String SEND_BUTTON_LABEL = "^";

    protected Client client;
    
    private GridBagLayout gbl;
    
    private GridBagConstraints gbc;
    
    private JScrollPane scrollPane;
    
    private JTextArea textOutput;
    
    private JTextField textInput;
    
    private JButton buttonSend;
    
    private Font font;

    public Chat()
    {
        super();
        initComponents();
        initScrollPane();
        initInputField();
        initSendButton();
    }

    private void initComponents()
    {
        this.font = new Font("Arial", Font.BOLD, 10);
        this.textOutput = new JTextArea();
        this.setFont(font);
        this.textOutput.setFont(font);
        this.textOutput.setEditable(false);
        this.scrollPane = new JScrollPane();
        this.scrollPane.setViewportView(this.textOutput);
        this.textInput = new JTextField();
        this.textInput.addActionListener(this);
        this.buttonSend = new JButton(SEND_BUTTON_LABEL);
        this.buttonSend.addActionListener(this);

        //add components
        this.gbl = new GridBagLayout();
        this.gbc = new GridBagConstraints();
        this.gbc.fill = GridBagConstraints.BOTH;
        this.setLayout(gbl);
    }

    private void initSendButton()
    {
        this.gbc.gridx = 1;
        this.gbc.gridy = 1;
        this.gbc.gridwidth = 1;
        this.gbc.gridheight = 1;
        this.gbc.weighty = 0;
        this.gbc.weightx = 0;
        this.gbl.setConstraints(buttonSend, gbc);
        this.add(buttonSend);
    }

    private void initInputField()
    {
        this.gbc.gridx = 0;
        this.gbc.gridy = 1;
        this.gbc.gridwidth = 1;
        this.gbc.gridheight = 1;
        this.gbc.weighty = 0;
        this.gbc.weightx = 1.0;
        this.gbl.setConstraints(textInput, gbc);
        this.add(textInput);
    }

    private void initScrollPane() 
    {
        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        this.gbc.gridwidth = 2;
        this.gbc.gridheight = 1;
        this.gbc.weighty = 1.0;
        this.gbc.weightx = 0;
        this.gbl.setConstraints(scrollPane, gbc);
        this.add(scrollPane);
    }

    /** 
     * Method of adding message to the list
     * @param str string to add
     */
    public void addMessage(String str) //added message to list
    {
        textOutput.append(str + "\n");
        textOutput.setCaretPosition(textOutput.getDocument().getLength());
    }

    /** 
     * Sending message method
     * @param arg0 event object
     */
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        this.getClient().sendMassage(textInput.getText());
        textInput.setText("");
    }

    /**
     * @return the client
     */
    public Client getClient()
    {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(Client client)
    {
        this.client = client;
    }
    
    @Override
    public void setEnabled(boolean enabled)
    {
      super.setEnabled(enabled);
      buttonSend.setEnabled(enabled);
      textInput.setEnabled(enabled);
      textOutput.setEnabled(enabled);
    }
}
