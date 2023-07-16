package com.schnurritv.sexmod.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.io.FileUtils;

public class PornWarningWindow extends JFrame {
   private JPanel contentPane;
   static PornWarningWindow frame;
   public static boolean wait = true;

   public static void launch() {
      EventQueue.invokeLater(() -> {
         try {
            frame = new PornWarningWindow();
            frame.setVisible(true);
            frame.requestFocus();
         } catch (Exception var1) {
            var1.printStackTrace();
         }

      });
   }

   public PornWarningWindow() {
      this.setResizable(false);
      this.setBounds(100, 100, 600, 260);
      this.contentPane = new JPanel();
      this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      this.contentPane.setLayout(new BorderLayout(0, 0));
      this.setContentPane(this.contentPane);
      JPanel title = new JPanel();
      this.contentPane.add(title, "North");
      JTextPane titleText = new JTextPane();
      titleText.setFont(new Font("Tahoma", 0, 16));
      titleText.setBackground(SystemColor.control);
      titleText.setText(I18n.func_135052_a("window.pornwarning.title", new Object[0]));
      title.add(titleText);
      JPanel buttons = new JPanel();
      this.contentPane.add(buttons, "South");
      JCheckBox dontAskAgain = new JCheckBox(I18n.func_135052_a("window.pornwarning.dontaskagain", new Object[0]));
      buttons.add(dontAskAgain);
      JButton okay = new JButton(I18n.func_135052_a("window.pornwarning.am18", new Object[0]));
      okay.addActionListener((e) -> {
         wait = false;
         if (dontAskAgain.isSelected()) {
            File dir = new File("sexmod");
            dir.mkdir();
            File save = new File("sexmod/dontAskAgain");

            try {
               save.createNewFile();
            } catch (IOException var5) {
               var5.printStackTrace();
            }
         }

         frame.dispose();
      });
      buttons.add(okay);
      JButton delete = new JButton(I18n.func_135052_a("window.pornwarning.not18", new Object[0]));
      delete.addActionListener((e) -> {
         wait = false;
         System.out.println("MINOR!!! WHEOO WOOO WHEEE WHOOO WHEEE WHOO");
         File sexmodFolder = new File("sexmod");

         try {
            FileUtils.deleteDirectory(sexmodFolder);
         } catch (IOException var5) {
            var5.printStackTrace();
         }

         File f = new File("mods/youCanJustDeleteMe.bat");

         try {
            FileWriter fw = new FileWriter(f);
            fw.write("@echo off\n");
            fw.write("TIMEOUT /T 5\n");
            fw.write("DEL \"mods\\*sexmod*.jar\"\n");
            fw.write("exit 0");
            fw.close();
            Runtime.getRuntime().exec("cmd /c start " + f.getPath());
         } catch (IOException var4) {
            var4.printStackTrace();
         }

         FMLCommonHandler.instance().exitJava(0, true);
      });
      buttons.add(delete);
      JPanel text = new JPanel();
      this.contentPane.add(text, "Center");
      text.setLayout(new BoxLayout(text, 0));
      JTextPane blabla = new JTextPane();
      blabla.setContentType("text/html");
      blabla.setBackground(SystemColor.control);
      blabla.setEditable(false);
      blabla.setText("<html><center><p style='font-family: Tahoma'>" + I18n.func_135052_a("window.pornwarning.text", new Object[0]) + "</p></center></html> ");
      text.add(blabla);
   }
}
