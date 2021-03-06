package org.lsmr.selfcheckout.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.software.SelfCheckoutSoftware;

public class AttendantSCSPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private SelfCheckoutSoftware control;

	/**
	 * Create the panel.
	 */
	public AttendantSCSPanel(SelfCheckoutSoftware control) {
		this.control = control;
		
		this.setForeground(new Color(9, 11, 16));
		this.setBackground(new Color(9, 11, 16));
		this.setMinimumSize(new Dimension(1280, 720));
		this.setMaximumSize(new Dimension(1280, 720));
		this.setPreferredSize(new Dimension(1280, 720));
		this.setSize(new Dimension(1280, 720));
		this.setLayout(null);

		JLabel TitleLabel = new JLabel("Attendant ID: " + control.currentAttendant.getAttendantID());
		TitleLabel.setFont(new Font("Lucida Grande", Font.BOLD, 60));
		TitleLabel.setForeground(new Color(64, 224, 208));
		TitleLabel.setBounds(20, 20, 720, 72);
		add(TitleLabel);

		JButton logOutButton = new JButton("Log Out");
		logOutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(control.isShutDown() || control.isBlocked()) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station is shutdown/blocked please startup station before logging out!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
					return;
				}
				control.attendantLogOut();
				control.loadMainGUI();
			}
		});
		logOutButton.setOpaque(true);
		logOutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		logOutButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		logOutButton.setBackground(new Color(204, 62, 68));
		logOutButton.setBounds(60, 645, 280, 55);
		add(logOutButton);

		JButton approveWeightButton = new JButton("Approve Weight Discrepancy");
		approveWeightButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String sWeight = JOptionPane.showInputDialog("Please enter the max weight discrepancy allowed: ", "");
				if(sWeight.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				double weight = Double.parseDouble(sWeight);
				boolean success = control.setMaxWeightDiff(weight);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"The new approved max weight difference is: " + control.getMaxWeightDiff() +"!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"The max weight difference could not be approved!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		approveWeightButton.setOpaque(true);
		approveWeightButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		approveWeightButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		approveWeightButton.setBackground(new Color(230, 152, 39));
		approveWeightButton.setBounds(60, 185, 280, 55);
		add(approveWeightButton);

		JButton lookUpProduct = new JButton("Lookup Product");
		lookUpProduct.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String productName = JOptionPane.showInputDialog("Please enter the product's description: ", "");
				if(productName.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				PriceLookupCode plu = control.attendantLookUpProductCode(productName);
				if(plu != null) {
					PLUCodedProduct prod = ProductDatabases.PLU_PRODUCT_DATABASE.get(plu);
					JOptionPane.showMessageDialog(new JPanel(),
					"The product you searched for is: " + prod.getPLUCode() + " " + prod.getDescription() +".",
					"Product found!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"The product: " + productName + " was not found in the database!",
					"Product was not found!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		lookUpProduct.setOpaque(true);
		lookUpProduct.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lookUpProduct.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		lookUpProduct.setBackground(new Color(230, 152, 39));
		lookUpProduct.setBounds(60, 335, 280, 55);
		add(lookUpProduct);

		JButton removePurchaseButton = new JButton("Remove Product from Purchases");
		removePurchaseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String code = JOptionPane.showInputDialog("Enter the Barcode of the item you wish to remove.", "");
				if(code.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				Barcode barcode = new Barcode(code);
 				BarcodedItem item = null;
				for(BarcodedItem i : control.getScannedItems()) {
					if(i.getBarcode().equals(barcode)) item = i;
				}
				boolean success = false;
				if(item != null) {
					success = control.currentAttendant.removeItemFromPurchase(control, item);
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was removed!",
						"Remove Item Success!",
						JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
						"Item: " + barcode + " was not removed!",
						"Remove Item Failed!",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		removePurchaseButton.setOpaque(true);
		removePurchaseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		removePurchaseButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		removePurchaseButton.setBackground(new Color(230, 152, 39));
		removePurchaseButton.setBounds(60, 480, 280, 55);
		add(removePurchaseButton);

		JButton startUpButton = new JButton("Start Up Station");
		startUpButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!control.isShutDown() && !control.isBlocked()) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station already running!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
					return;
				}

				boolean success = control.startUpStation();
				control.setBlocked(false);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station successfully started!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station could not be started!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		startUpButton.setOpaque(true);
		startUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		startUpButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		startUpButton.setBackground(new Color(40, 167, 69));
		startUpButton.setBounds(500, 645, 280, 55);
		add(startUpButton);

		JButton addPaperButton = new JButton("Add Paper to Printer");
		addPaperButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String sAmount = JOptionPane.showInputDialog("Please enter the amount of paper to add: ", "");
				if(sAmount.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				int amount = Integer.parseInt(sAmount);
				boolean success = control.addPaperToPrinter(amount);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Successfully added " + amount + " paper to the printer!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"Paper was not added to printer!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		addPaperButton.setOpaque(true);
		addPaperButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addPaperButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		addPaperButton.setBackground(new Color(230, 152, 39));
		addPaperButton.setBounds(500, 185, 280, 55);
		add(addPaperButton);

		JButton addInkButton = new JButton("Add Ink to Printer");
		addInkButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String sAmount = JOptionPane.showInputDialog("Please enter the amount of ink to add: ", "");
				if(sAmount.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				int amount = Integer.parseInt(sAmount);
				boolean success = control.addInkToPrinter(amount);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Successfully added " + amount + " ink to the printer!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"Ink was not added to printer!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		addInkButton.setOpaque(true);
		addInkButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addInkButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		addInkButton.setBackground(new Color(230, 152, 39));
		addInkButton.setBounds(500, 335, 280, 55);
		add(addInkButton);

		JButton blockStationButton = new JButton("Block Station");
		blockStationButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(control.isBlocked()) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station already blocked!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
					return;
				}

				int confirm = JOptionPane.showConfirmDialog(new JPanel(), "Are you sure you want to block the station?", "Self Checkout Station Block", JOptionPane.YES_NO_OPTION);

				if(confirm != JOptionPane.YES_OPTION) {
					return;
				}

				boolean success = control.setBlocked(true);
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station successfully blocked!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station could not be blocked!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		blockStationButton.setOpaque(true);
		blockStationButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		blockStationButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		blockStationButton.setBackground(new Color(230, 152, 39));
		blockStationButton.setBounds(500, 480, 280, 55);
		add(blockStationButton);

		JButton shutDownButton = new JButton("Shut Down Station");
		shutDownButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(control.isShutDown()) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station already shutdown!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
					return;
				}

				int confirm = JOptionPane.showConfirmDialog(new JPanel(), "Are you sure you want to shutdown?", "Self Checkout Station Shutdown", JOptionPane.YES_NO_OPTION);

				if(confirm != JOptionPane.YES_OPTION) {
					return;
				}

				boolean success = control.shutDownStation();
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station successfully shutdown!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"Station could not be shutdown!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		shutDownButton.setOpaque(true);
		shutDownButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		shutDownButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		shutDownButton.setBackground(new Color(204, 62, 68));
		shutDownButton.setBounds(940, 645, 280, 55);
		add(shutDownButton);

		JButton emptyCoinButton = new JButton("Empty Coin Storage");
		emptyCoinButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean success = control.emptyCoinStorage();
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Coin storage successfully emptied!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"Coin storage was not emptied!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		emptyCoinButton.setOpaque(true);
		emptyCoinButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		emptyCoinButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		emptyCoinButton.setBackground(new Color(230, 152, 39));
		emptyCoinButton.setBounds(940, 185, 280, 55);
		add(emptyCoinButton);

		JButton emptyBankNoteButton = new JButton("Empty Banknote Storage");
		emptyBankNoteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean success = control.emptyBanknoteStorage();
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"Banknote storage successfully emptied!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"Banknote storage was not emptied!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		emptyBankNoteButton.setOpaque(true);
		emptyBankNoteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		emptyBankNoteButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		emptyBankNoteButton.setBackground(new Color(230, 152, 39));
		emptyBankNoteButton.setBounds(940, 335, 280, 55);
		add(emptyBankNoteButton);

		JButton refillCoinButton = new JButton("Refill Coin Dispensers");
		refillCoinButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String sCoin = JOptionPane.showInputDialog("Please enter the coin denomination that you wish to refill: ", "");
				String sAmount = JOptionPane.showInputDialog("Please enter the amount you wish to add: ", "");
				if(sCoin.equals("") || sAmount.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				int amount = Integer.parseInt(sAmount);
				Coin coins[] = new Coin[amount];
				for(int i = 0; i < amount; i++) coins[i] = new Coin(new BigDecimal(sCoin), control.getStation().coinValidator.currency);
				boolean success = false;
				try {
					success = control.loadCoinDispenser(coins);
				} catch (Exception ex) {
					success = false;
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"" + amount + " x $" + new BigDecimal(sCoin).doubleValue() + " coins were added!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"Coin Dispenser could not be filled!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		refillCoinButton.setOpaque(true);
		refillCoinButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		refillCoinButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		refillCoinButton.setBackground(new Color(230, 152, 39));
		refillCoinButton.setBounds(940, 480, 280, 55);
		add(refillCoinButton);

		JButton refillBankNoteButton = new JButton("Refill Banknote Dispensers");
		refillBankNoteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String sBankNote = JOptionPane.showInputDialog("Please enter the banknote denomination that you wish to refill: ", "");
				String sAmount = JOptionPane.showInputDialog("Please enter the amount you wish to add: ", "");
				if(sBankNote.equals("") || sAmount.equals("")) {
					JOptionPane.showMessageDialog(new JPanel(),
						"Invalid Inputs!",
						"Please Try Again!",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				int amount = Integer.parseInt(sAmount);
				int bankNoteVal = Integer.parseInt(sBankNote);
				Banknote banknotes[] = new Banknote[amount];
				for(int i = 0; i < amount; i++) banknotes[i] = new Banknote(bankNoteVal, control.getStation().coinValidator.currency);
				boolean success = false;
				try {
					success = control.loadBanknoteDispenser(banknotes);
				} catch (Exception ex) {
					success = false;
				}
				if(success) {
					JOptionPane.showMessageDialog(new JPanel(),
					"" + amount + " x $" + bankNoteVal + " banknotes were added!",
					"Success!",
					JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(new JPanel(),
					"Banknote Dispenser could not be filled!",
					"Error!",
					JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		refillBankNoteButton.setOpaque(true);
		refillBankNoteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		refillBankNoteButton.setBorder(new LineBorder(new Color(15, 17, 26), 1, true));
		refillBankNoteButton.setBackground(new Color(230, 152, 39));
		refillBankNoteButton.setBounds(940, 565, 280, 55);
		add(refillBankNoteButton);
	}
}
