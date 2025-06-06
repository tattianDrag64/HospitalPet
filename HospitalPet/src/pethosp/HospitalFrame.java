package pethosp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class HospitalFrame extends JFrame {
	private static final long serialVersionUID = 4L;

	Connection conn = MyDBConnection.getConnection();
	PreparedStatement state = null;
	ResultSet result = null;
	int idpet = -1, idown = -1, idvis = -1;

	JTabbedPane tab = new JTabbedPane();
	
	//PET PANEL------------------------------------------------------

	JPanel petsPanel = new JPanel(new BorderLayout());
	JPanel petsUpPanel = new JPanel(new GridLayout(8, 2));
	JPanel petsMidPanel = new JPanel();
	JPanel petsDownPanel = new JPanel();

	JLabel petNameL = new JLabel("Name");
	JLabel petSpeciesL = new JLabel("Species");
	DatePicker petBirthDatePicker;
	JLabel petOwnerL = new JLabel("Owner");

	JTextField petNameTF = new JTextField();
	JTextField petSpeciesTF = new JTextField();
	JTextField petBirthDateTF = new JTextField();
	JComboBox<String> comboOwn = new JComboBox<>();

	JButton petAddBtn = new JButton("Add");
	JButton petEditBtn = new JButton("Edit");
	JButton petDeleteBtn = new JButton("Delete");
	JButton petSearchBtn = new JButton("Search");

	JTable petTable = new JTable();
	JScrollPane petScroll = new JScrollPane(petTable);
	
	//OWNER PANEL------------------------------------------------------

	JPanel OwnerPanel = new JPanel(new BorderLayout());
	JPanel OwnerUpPanel = new JPanel(new GridLayout(6, 2));
	JPanel OwnerMidPanel = new JPanel();
	JPanel OwnerDownPanel = new JPanel();

	JLabel OwnerNameL = new JLabel("Name");
	JLabel OwnerPhoneL = new JLabel("Phone");
	JLabel OwnerEmailL = new JLabel("Email");

	JTextField OwnerNameTF = new JTextField();
	JTextField OwnerPhoneTF = new JTextField();
	JTextField OwnerEmailTF = new JTextField();

	JButton OwnAddBtn = new JButton("Add");
	JButton OwnEditBtn = new JButton("Edit");
	JButton OwnDeleteBtn = new JButton("Delete");
	JButton OwnSearchBtn = new JButton("Search");

	JTable OwnTable = new JTable();
	JScrollPane OwnScroll = new JScrollPane(OwnTable);
	
	//VISUAL PANEL------------------------------------------------------

	JPanel VisPanel = new JPanel(new BorderLayout());
	JPanel VisUpPanel = new JPanel(new GridLayout(6, 2));
	JPanel VisMidPanel = new JPanel();
	JPanel VisDownPanel = new JPanel();

	JLabel VisPetL = new JLabel("Pet's Name");
	DatePicker visDatePicker;
	JLabel VisDiagnosisL = new JLabel("Diagnosis Description");
	JComboBox<String> comboPet = new JComboBox<>();
	
	JTextField VisDateVisitTF = new JTextField();
	JTextField VisDiagnosisTF = new JTextField();

	JButton VisAddBtn = new JButton("Add");
	JButton VisEditBtn = new JButton("Edit");
	JButton VisDeleteBtn = new JButton("Delete");
	JButton VisSearchBtn = new JButton("Search");

	JTable VisTable = new JTable();
	JScrollPane VisScroll = new JScrollPane(VisTable);

	//SEARCH PANEL------------------------------------------------------
	
	JPanel searchPanel = new JPanel(new BorderLayout());
	JPanel searchTopPanel = new JPanel(new GridLayout(4, 2));
	JPanel searchButtonPanel = new JPanel();
	JPanel searchResultPanel = new JPanel();

	JLabel searchSpeciesLabel = new JLabel("Species:");
	DatePicker searchDatePicker;
	JTextField searchSpeciesTF = new JTextField();
	JButton searchBtn = new JButton("Search");
	JTable searchTable = new JTable();
	JScrollPane searchScroll = new JScrollPane(searchTable);

	//REFRESH------------------------------------------------------
	
	JButton refreshAllTablesBtn = new JButton("Refresh");

	public HospitalFrame() {
		setTitle("Hospital for Pets");
		setSize(600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setupPetsPanel();
		setupOwnersPanel();
		setupVisitsPanel();
		setupVisitSearchByDateAndSpeciesPanel();

		tab.addTab("Pets", petsPanel);
		tab.addTab("Owners", OwnerPanel);
		tab.addTab("Visits", VisPanel);
		tab.addTab("Search Visits", searchPanel);

		add(tab, BorderLayout.CENTER);
		add(refreshAllTablesBtn, BorderLayout.SOUTH);
		refreshAllTablesBtn.addActionListener(new RefreshAllTablesAction());
		setVisible(true);
	}

	private void setupPetsPanel() {
		petsUpPanel.add(petNameL);
		petsUpPanel.add(petNameTF);
		petsUpPanel.add(petSpeciesL);
		petsUpPanel.add(petSpeciesTF);
		
		DatePickerSettings settings = new DatePickerSettings();
		settings.setFormatForDatesCommonEra("yyyy-MM-dd");
		petBirthDatePicker = new DatePicker(settings);
		
		petsUpPanel.add(new JLabel("Birth Date:"));
		petsUpPanel.add(petBirthDatePicker);
		petsUpPanel.add(petOwnerL);
		petsUpPanel.add(comboOwn);

		petsMidPanel.add(petAddBtn);
		petsMidPanel.add(petEditBtn);
		petsMidPanel.add(petDeleteBtn);
		petsMidPanel.add(petSearchBtn);

		petAddBtn.addActionListener(new AddPetAction());
		petDeleteBtn.addActionListener(new DeletePetAction());
		petEditBtn.addActionListener(new EditPetAction());
		petSearchBtn.addActionListener(new SearchPetAction());

		loadOwnersToComboBox();
		loadPetsToTable();
		petScroll.setPreferredSize(new Dimension(550, 250));
		petsDownPanel.add(petScroll);

		petsPanel.add(petsUpPanel, BorderLayout.NORTH);
		petsPanel.add(petsMidPanel, BorderLayout.CENTER);
		petsPanel.add(petsDownPanel, BorderLayout.SOUTH);
		
		petTable.addMouseListener(new PetMouseAction());
	}

	private void setupOwnersPanel() {
		OwnerUpPanel.add(OwnerNameL);
		OwnerUpPanel.add(OwnerNameTF);
		OwnerUpPanel.add(OwnerPhoneL);
		OwnerUpPanel.add(OwnerPhoneTF);
		OwnerUpPanel.add(OwnerEmailL);
		OwnerUpPanel.add(OwnerEmailTF);

		OwnerMidPanel.add(OwnAddBtn);
		OwnerMidPanel.add(OwnEditBtn);
		OwnerMidPanel.add(OwnDeleteBtn);
		OwnerMidPanel.add(OwnSearchBtn);

		OwnAddBtn.addActionListener(new AddOwnerAction());
		OwnDeleteBtn.addActionListener(new DeleteOwnerAction());
		OwnEditBtn.addActionListener(new EditOwnerAction());
		OwnSearchBtn.addActionListener(new SearchOwnerAction());

		loadOwnersToTable();
		OwnScroll.setPreferredSize(new Dimension(550, 250));
		OwnerDownPanel.add(OwnScroll);

		OwnerPanel.add(OwnerUpPanel, BorderLayout.NORTH);
		OwnerPanel.add(OwnerMidPanel, BorderLayout.CENTER);
		OwnerPanel.add(OwnerDownPanel, BorderLayout.SOUTH);

		OwnTable.addMouseListener(new OwnerMouseAction());
	}

	private void setupVisitsPanel() {
		VisUpPanel.add(VisPetL);
		VisUpPanel.add(comboPet);
		
		DatePickerSettings settings = new DatePickerSettings();
		settings.setFormatForDatesCommonEra("yyyy-MM-dd");
		visDatePicker = new DatePicker(settings);
		
		VisUpPanel.add(new JLabel("Visit Date:"));
		VisUpPanel.add(visDatePicker);
		VisUpPanel.add(VisDiagnosisL);
		VisUpPanel.add(VisDiagnosisTF);

		VisMidPanel.add(VisAddBtn);
		VisMidPanel.add(VisEditBtn);
		VisMidPanel.add(VisDeleteBtn);
		VisMidPanel.add(VisSearchBtn);
		
		VisAddBtn.addActionListener(new AddVisAction());
		VisDeleteBtn.addActionListener(new DeleteVisAction());
		VisEditBtn.addActionListener(new EditVisAction());
		VisSearchBtn.addActionListener(new SearchVisAction());
		
		loadPetsToComboBox();
		loadVisToTable();

		VisScroll.setPreferredSize(new Dimension(550, 250));
		VisDownPanel.add(VisScroll);

		VisPanel.add(VisUpPanel, BorderLayout.NORTH);
		VisPanel.add(VisMidPanel, BorderLayout.CENTER);
		VisPanel.add(VisDownPanel, BorderLayout.SOUTH);
		
		VisTable.addMouseListener(new VisMouseAction());
	}

	private void setupVisitSearchByDateAndSpeciesPanel() {
		DatePickerSettings settings = new DatePickerSettings();
		settings.setFormatForDatesCommonEra("yyyy-MM-dd");
		searchDatePicker = new DatePicker(settings);
		
		searchTopPanel.add(new JLabel("Visit Date:"));
		searchTopPanel.add(searchDatePicker);
		searchTopPanel.add(searchSpeciesLabel);
		searchTopPanel.add(searchSpeciesTF);

		searchButtonPanel.add(searchBtn);
		searchBtn.addActionListener(new SearchAction());

		searchScroll.setPreferredSize(new Dimension(550, 250));
		searchResultPanel.add(searchScroll);

		searchPanel.add(searchTopPanel, BorderLayout.NORTH);
		searchPanel.add(searchButtonPanel, BorderLayout.CENTER);
		searchPanel.add(searchResultPanel, BorderLayout.SOUTH);
	}
	
	//PETS PANEL------------------------------------------------------
	
	private void loadOwnersToComboBox() {
		comboOwn.removeAllItems();
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT name FROM Owners")) {
			comboOwn.addItem("-- All Owners --");
			while (rs.next()) {
				comboOwn.addItem(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading owners", "Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void loadPetsToTable() {
		try (Statement stmt = conn.createStatement();
		     ResultSet rs = stmt.executeQuery(
		         "SELECT Pets.id, Pets.name, Pets.species, Pets.birth_date, Owners.name AS owner_name " +
		         "FROM Pets JOIN Owners ON Pets.owner_id = Owners.id")) {

			MyModel model = new MyModel(rs);

			petTable.setModel(new javax.swing.table.AbstractTableModel() {
				public int getRowCount() {
					return model.getRowCount();
				}
				public int getColumnCount() {
					return model.getColumnCount();
				}
				public Object getValueAt(int r, int c) {
					return model.getValueAt(r, c);
				}
				public String getColumnName(int c) {
					return model.getColumnName(c); 
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading pets table", "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	class PetMouseAction implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			int row = petTable.getSelectedRow();
			if (row == -1)
				return; 
			try {
				idpet = Integer.parseInt(petTable.getValueAt(row, 0).toString());
				petNameTF.setText(petTable.getValueAt(row, 1).toString());
				petSpeciesTF.setText(petTable.getValueAt(row, 2).toString());
				String dateStr = petTable.getValueAt(row, 3).toString();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            LocalDate date = LocalDate.parse(dateStr, formatter);
	            petBirthDatePicker.setDate(date); 
				comboOwn.setSelectedItem(petTable.getValueAt(row, 4).toString());
	            
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error choosing row", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
	}
	
	private class AddPetAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String ownerQuery = "SELECT id FROM Owners WHERE name = ?";
				PreparedStatement ownerState = conn.prepareStatement(ownerQuery);
				ownerState.setString(1, (String) comboOwn.getSelectedItem());
				ResultSet rs = ownerState.executeQuery();
				
				String name = petNameTF.getText().trim();
				String species = petSpeciesTF.getText().trim();
				LocalDate birthDate = petBirthDatePicker.getDate();
				String selectedOwner = (String) comboOwn.getSelectedItem();
				
				if (name.isEmpty() || species.isEmpty() || birthDate == null || (selectedOwner.isEmpty())) {
					JOptionPane.showMessageDialog(null, "All fields must be filled in.", "Error", JOptionPane.WARNING_MESSAGE);
			        return; 
			    }

				if (!rs.next()) {
					JOptionPane.showMessageDialog(null, "Owner not found.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				idown = rs.getInt("id");
				String insertPet = "INSERT INTO Pets (name, species, birth_date, owner_id) VALUES (?, ?, ?, ?)";
				state = conn.prepareStatement(insertPet);
				state.setString(1, name);
				state.setString(2, species);
				state.setDate(3, java.sql.Date.valueOf(birthDate));
				state.setInt(4, idown);
				state.execute();
				JOptionPane.showMessageDialog(null, "Pet added successfully!");
				clearPetForm();    
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error adding pet", "Database Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class EditPetAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String sql = "update Pets set name = ?, species = ?, birth_date = ?, owner_id = ? where id = ?";
			try {	
				LocalDate selectedDate = petBirthDatePicker.getDate();
	            if (selectedDate == null) {
	                JOptionPane.showMessageDialog(null, "Please select a valid birth date.", "Input Error", JOptionPane.WARNING_MESSAGE);
	                return;
	            }
	            
	            String ownerQuery = "SELECT id FROM Owners WHERE name = ?";
	            PreparedStatement ownerStmt = conn.prepareStatement(ownerQuery);
	            ownerStmt.setString(1, (String) comboOwn.getSelectedItem());
	            ResultSet rs = ownerStmt.executeQuery();
	            if (!rs.next()) {
	                JOptionPane.showMessageDialog(null, "Owner not found.", "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }
	            idown = rs.getInt("id");
				state = conn.prepareStatement(sql);
				state.setString(1, petNameTF.getText());
				state.setString(2, petSpeciesTF.getText());
				state.setString(3, selectedDate.toString()); 
		        state.setInt(4, idown);
		        state.setInt(5, idpet); 
				
				int rows = state.executeUpdate();
	            
	            if(rows > 0) {
	            	JOptionPane.showMessageDialog(null, "Pet updated successfully!");
	            	loadOwnersToComboBox();
	            	idpet = -1;
	            } else {
	                JOptionPane.showMessageDialog(null, "Update failed. Pet not found.");
	            }
	            clearPetForm();
			} catch (SQLException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error updating pet", "Database Error",
	                    JOptionPane.ERROR_MESSAGE);
		}
		}
	}
	
	private class SearchPetAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String nameImput = petNameTF.getText().trim().toLowerCase().replaceAll("\\s+", " ");
			String speciesImput = petSpeciesTF.getText().trim().toLowerCase().replaceAll("\\s+", " ");
			LocalDate birthDateImput = petBirthDatePicker.getDate();
			String owberImput = (String)comboOwn.getSelectedItem();
			
			String sql = "select Pets.id, Pets.name, Pets.species, Pets.birth_date, Owners.name AS owner_name "
					+ "from Pets INNER JOIN Owners ON Pets.owner_id = Owners.id WHERE 1=1";
			List<String> parameters = new ArrayList<>();
			if(!nameImput.isEmpty()) {
				String[] words = nameImput.split(" ");
				for(String word : words) {
					sql += " and lower(Pets.name) like ?";
					parameters.add("%" + word + "%");
				}
			}
			
			if(!speciesImput.isEmpty()) {
				String[] words = speciesImput.split(" ");
				for(String word : words) {
					sql += " and lower(Pets.species) like ?";
					parameters.add("%" + word + "%");
				}
			}
			
			if(birthDateImput!=null) {
				sql+=" and Pets.birth_date = ?";
				parameters.add(birthDateImput.toString());
			}
				
			if (owberImput != null && !owberImput.trim().isEmpty() && !owberImput.equals("-- All Owners --")) {
			    sql += " and lower(Owners.name) like ?";
			    parameters.add("%" + owberImput.toLowerCase().trim() + "%");
			}

			try {
	            state = conn.prepareStatement(sql);
	            for (int i = 0; i < parameters.size(); i++)
	            	state.setString(i + 1, parameters.get(i));
	            ResultSet rs = state.executeQuery();
	            if (!rs.isBeforeFirst()) { 
	                JOptionPane.showMessageDialog(null, "No pets found matching the criteria.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
	                petTable.setModel(new DefaultTableModel());
	            } else {
	                petTable.setModel(new MyModel(rs));
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error during search", "Database Error", JOptionPane.ERROR_MESSAGE);
	        } catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private class DeletePetAction implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        if (idpet == -1) {
	            JOptionPane.showMessageDialog(null, "Please select a pet to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        int confirm = JOptionPane.showConfirmDialog(null,
	                "Are you sure you want to delete this pet?\nAll visits related to this pet will also be deleted.",
	                "Confirm Delete", JOptionPane.YES_NO_OPTION);

	        if (confirm != JOptionPane.YES_OPTION) return;
	        try {
	            state = conn.prepareStatement("DELETE FROM Pets WHERE id = ?");
	            state.setInt(1, idpet);
	            int rows = state.executeUpdate();
	            if (rows > 0) {
	                JOptionPane.showMessageDialog(null, "Pet deleted successfully!");
	                idpet = -1;
	            } else {
	                JOptionPane.showMessageDialog(null, "Pet not found.");
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error deleting pet", "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

	private void clearPetForm() {
	    petNameTF.setText("");
	    petSpeciesTF.setText("");
	    petBirthDatePicker.setDate(null); 
	    comboOwn.setSelectedIndex(0); 
	    idpet = -1; 
	}
	
	//OWNER PANEL------------------------------------------------------
	
	private void loadOwnersToTable() {
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Owners")) {
			MyModel model = new MyModel(rs);
			JTable table = new JTable(new javax.swing.table.AbstractTableModel() {
				public int getRowCount() {
					return model.getRowCount();
				}

				public int getColumnCount() {
					return model.getColumnCount();
				}

				public Object getValueAt(int r, int c) {
					return model.getValueAt(r, c);
				}

				public String getColumnName(int c) {
					return model.getColumnName(c);
				}
			});
			OwnTable.setModel(table.getModel());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading owners table", "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	class OwnerMouseAction implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			int row = OwnTable.getSelectedRow();
			if (row == -1)
				return;
			try {
				idown = Integer.parseInt(OwnTable.getValueAt(row, 0).toString());
				OwnerNameTF.setText(OwnTable.getValueAt(row, 1).toString());
				OwnerPhoneTF.setText(OwnTable.getValueAt(row, 2).toString());
				OwnerEmailTF.setText(OwnTable.getValueAt(row, 3).toString());
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error choosing row", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
	}
	
	private class AddOwnerAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String sql = "insert into Owners(name, phone, email) VALUES (?, ?, ?)";
			try {
				String name = OwnerNameTF.getText().trim();
				String phone = OwnerPhoneTF.getText().trim();
				String email = OwnerEmailTF.getText().trim();
				
				if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
					JOptionPane.showMessageDialog(null, "All fields must be filled in.", "Error", JOptionPane.WARNING_MESSAGE);
			        return; 
			    }
				state = conn.prepareStatement(sql);
				state.setString(1, name);
				state.setString(2, phone);
				state.setString(3, email);
				state.execute();
				
				Statement stmt = conn.createStatement();
				clearOwnerForm();
				JOptionPane.showMessageDialog(null, "Owner added successfully!");

			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error adding owner", "Database Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class EditOwnerAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String sql = "update Owners set name = ?, phone = ?, email= ? where id = ?";
			try {
				state = conn.prepareStatement(sql);
				state.setString(1, OwnerNameTF.getText());
	            state.setString(2, OwnerPhoneTF.getText());
	            state.setString(3, OwnerEmailTF.getText());
	            state.setInt(4, idown);
	            int rows = state.executeUpdate();
	            if(rows > 0) {
	            	JOptionPane.showMessageDialog(null, "Owner updated successfully!");
	            	loadOwnersToComboBox();
	            	idown = -1;
	            } else {
	                JOptionPane.showMessageDialog(null, "Update failed. Owner not found.");
	            }
	            clearOwnerForm();
			} catch (SQLException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error updating owner", "Database Error",
	                    JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private class SearchOwnerAction implements ActionListener {
		@Override
	    public void actionPerformed(ActionEvent e) {
	    	String nameInput = OwnerNameTF.getText().trim().toLowerCase().replaceAll("\\s+", " ");
	        String phoneInput = OwnerPhoneTF.getText().trim();
	        String emailInput = OwnerEmailTF.getText().trim().toLowerCase().replaceAll("\\s+", " ");

	        String sql = "SELECT * FROM Owners WHERE 1=1";
	        List<String> parameters = new ArrayList<>();
	        if (!nameInput.isEmpty()) {
	            String[] words = nameInput.split(" ");
	            for (String word : words) {
	                sql += " AND LOWER(name) LIKE ?";
	                parameters.add("%" + word + "%");
	            }
	        }

	        if (!phoneInput.isEmpty()) {
	            sql += " AND phone LIKE ?";
	            parameters.add("%" + phoneInput + "%");
	        }

	        if (!emailInput.isEmpty()) {
	            String[] words = emailInput.split(" ");
	            for (String word : words) {
	                sql += " AND LOWER(email) LIKE ?";
	                parameters.add("%" + word + "%");
	            }
	        }

	        try {
	            state = conn.prepareStatement(sql);
	            for (int i = 0; i < parameters.size(); i++)
	            	state.setString(i + 1, parameters.get(i));
	            ResultSet rs = state.executeQuery();
	            if (!rs.isBeforeFirst()) { 
	                JOptionPane.showMessageDialog(null, "No owners found matching the criteria.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
	                OwnTable.setModel(new DefaultTableModel());
	            } else {
	            	OwnTable.setModel(new MyModel(rs));
	            }
	            OwnTable.setModel(new MyModel(state.executeQuery()));

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error during search", "Database Error", JOptionPane.ERROR_MESSAGE);
	        } catch (Exception e1) {
				e1.printStackTrace();
			}
	    }
	}

	private class DeleteOwnerAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int confirm = JOptionPane.showConfirmDialog(null,
	                "Are you sure you want to delete this owner?",
	                "Confirm Delete", JOptionPane.YES_NO_OPTION);

	        if (confirm != JOptionPane.YES_OPTION) return;
			try {
				state = conn.prepareStatement("delete from Owners where id=?");
				state.setInt(1, idown);
				int rowsAffected = state.executeUpdate();
				if (rowsAffected > 0) {
	                JOptionPane.showMessageDialog(null, "Owner deleted successfully!");
	                idown = -1;
	                loadOwnersToComboBox();
				} else {
	                JOptionPane.showMessageDialog(null, "Owner not found or already deleted.");
	            }

			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error deleting owner", "Database Error",
						JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error deleting owner", "Database Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void clearOwnerForm() {
	    OwnerNameTF.setText("");
	    OwnerEmailTF.setText("");
	    OwnerPhoneTF.setText("");
	    idown = -1; 
	}
	
	//VISITS PANEL------------------------------------------------------
	
	private void loadPetsToComboBox() {
		comboPet.removeAllItems();
		try (Statement state = conn.createStatement();
			ResultSet rs = state.executeQuery("SELECT name FROM Pets");) {
			comboPet.addItem("-- All Pets --");
			while(rs.next()) comboPet.addItem(rs.getString("name"));
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading pets", "Database Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadVisToTable() {
		try {
			state = conn.prepareStatement("select Visits.id, Pets.name as pet_name, Visits.visit_date, Visits.diagnosis, Owners.name as owner_name "
					+ "from Visits join Pets on Visits.pet_id = Pets.id "
					+ "join Owners on Pets.owner_id = Owners.id");
			ResultSet rs = state.executeQuery();
			MyModel model = new MyModel(rs);
			VisTable.setModel(new javax.swing.table.AbstractTableModel() {
				public int getRowCount() {
					return model.getRowCount();
				}
				public int getColumnCount() {
					return model.getColumnCount();
				}
				public Object getValueAt(int r, int c) {
					return model.getValueAt(r, c);
				}
				public String getColumnName(int c) {
					return model.getColumnName(c); // Автоматически owner_name
				}
			});	
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error loading visits table", "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	class VisMouseAction implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			int row = VisTable.getSelectedRow();
			if (row == -1)
				return; 
			try {
				idvis = Integer.parseInt(VisTable.getValueAt(row, 0).toString());
				comboPet.setSelectedItem(VisTable.getValueAt(row, 1).toString());
				String dateStr = VisTable.getValueAt(row, 2).toString();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            LocalDate date = LocalDate.parse(dateStr, formatter);
	            visDatePicker.setDate(date); 
	            VisDiagnosisTF.setText(VisTable.getValueAt(row, 3).toString());
				comboOwn.setSelectedItem(VisTable.getValueAt(row, 4).toString());
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error choosing row", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
	}
	
	private class AddVisAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String petQuery = "select id from Pets where name = ?";
			try {
				PreparedStatement petState = conn.prepareStatement(petQuery);
				petState.setString(1, (String) comboPet.getSelectedItem());
				ResultSet rs = petState.executeQuery();
				
				String diagnosis = VisDiagnosisTF.getText().trim();
				LocalDate visitDate = visDatePicker.getDate();
				String selectedPet = (String) comboOwn.getSelectedItem();
				
				if (diagnosis.isEmpty() || visitDate == null || (selectedPet.isEmpty())) {
					JOptionPane.showMessageDialog(null, "All fields must be filled in.", "Error", JOptionPane.WARNING_MESSAGE);
			        return; 
			    }
				if(!rs.next()) {
					JOptionPane.showMessageDialog(null, "Pet not found.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				idpet = rs.getInt("id");			
				state = conn.prepareStatement("insert into Visits(pet_id, visit_date, diagnosis) values (?, ?, ?)");
		        state.setInt(1, idpet);
		        state.setDate(2, java.sql.Date.valueOf(visitDate));
		        state.setString(3, diagnosis);
		        state.execute();
				JOptionPane.showMessageDialog(null, "Visit added successfully!");
				clearVisitForm();
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error adding visit", "Database Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class EditVisAction implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        String sql = "update Visits set pet_id = ?, visit_date = ?, diagnosis = ? where id = ?";
	        try {
	            LocalDate selectedDate = visDatePicker.getDate();
	            if (selectedDate == null) {
	                JOptionPane.showMessageDialog(null, "Please select a valid visit date.", "Input Error", JOptionPane.WARNING_MESSAGE);
	                return;
	            }
	            String petQuery = "select id from Pets where name= ?";
	            PreparedStatement petStmt = conn.prepareStatement(petQuery);
	            petStmt.setString(1, (String) comboPet.getSelectedItem());
	            ResultSet rs = petStmt.executeQuery();
	            if (!rs.next()) {
	                JOptionPane.showMessageDialog(null, "Pet not found.", "Error", JOptionPane.ERROR_MESSAGE);
	                return;
	            }
	            idpet = rs.getInt("id");

	            state = conn.prepareStatement(sql);
	            state.setInt(1, idpet);
	            state.setDate(2, java.sql.Date.valueOf(selectedDate));
	            state.setString(3, VisDiagnosisTF.getText());
	            state.setInt(4, idvis);

	            int rows = state.executeUpdate();
	            if (rows > 0) {
	                JOptionPane.showMessageDialog(null, "Visit updated successfully!");
	                idvis = -1;
	            } else {
	                JOptionPane.showMessageDialog(null, "Update failed. Visit not found.");
	            }
	            clearVisitForm();

	        } catch (SQLException e1) {
	            e1.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Database error during update.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	
	private class SearchVisAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String petImput = (String)comboPet.getSelectedItem();
			LocalDate visitDateImput = visDatePicker.getDate();
			String diagnosisImput = VisDiagnosisTF.getText().trim().toLowerCase().replaceAll("\\s+", " ");
			
			String sql = "SELECT Visits.id, Pets.name AS pet_name, Visits.visit_date, Visits.diagnosis, Owners.name AS owner_name "
		               + "FROM Visits "
		               + "JOIN Pets ON Visits.pet_id = Pets.id "
		               + "JOIN Owners ON Pets.owner_id = Owners.id "
		               + "WHERE 1=1";
			List<String> parameters = new ArrayList<>();
			if(petImput != null && !petImput.trim().isEmpty() && !petImput.equals("-- All Pets --")) {
				sql+=" and lower(Pets.name) like ?";
				parameters.add("%" + petImput.toLowerCase().trim() + "%");
			}
			
			if(visitDateImput!=null) {
				sql+=" and Visits.visit_date = ?";
				parameters.add(visitDateImput.toString());
			}
			
			if (!diagnosisImput.isEmpty()) {
		        String[] words = diagnosisImput.split(" ");
		        for (String word : words) {
		            sql += " AND LOWER(Visits.diagnosis) LIKE ?";
		            parameters.add("%" + word + "%");
		        }
		    }
			
			try {
	            state = conn.prepareStatement(sql);
	            for (int i = 0; i < parameters.size(); i++)
	            	state.setString(i + 1, parameters.get(i));
	            ResultSet rs = state.executeQuery();
	            if (!rs.isBeforeFirst()) { 
	                JOptionPane.showMessageDialog(null, "No visits found matching the criteria.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
	                VisTable.setModel(new DefaultTableModel()); 
	            } else {
	            	VisTable.setModel(new MyModel(rs));
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error during search", "Database Error", JOptionPane.ERROR_MESSAGE);
	        } catch (Exception e1) {
				e1.printStackTrace();
			}
		} 
	}

	private class DeleteVisAction implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        if (idvis == -1) {
	            JOptionPane.showMessageDialog(null, "Please select a visit to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        int confirm = JOptionPane.showConfirmDialog(null,
	                "Are you sure you want to delete this visit?",
	                "Confirm Delete", JOptionPane.YES_NO_OPTION);

	        if (confirm != JOptionPane.YES_OPTION) return;
	        try {
	            state = conn.prepareStatement("DELETE FROM Visits WHERE id = ?");
	            state.setInt(1, idvis);
	            int rows = state.executeUpdate();
	            if (rows > 0) {
	                JOptionPane.showMessageDialog(null, "Visit deleted successfully!");
	                idvis = -1;
	            } else {
	                JOptionPane.showMessageDialog(null, "Visit not found.");
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error deleting visit", "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

	private void clearVisitForm() {
	    comboPet.setSelectedIndex(0); 
	    visDatePicker.setDate(null);
	    VisDiagnosisTF.setText("");
	    idvis = -1; 
	}

	//SEARCH PANEL------------------------------------------------------
	
	private class SearchAction implements ActionListener {

	    @Override
	    public void actionPerformed(ActionEvent e) {
	        LocalDate visitDateInput = searchDatePicker.getDate();
	        String speciesInput = searchSpeciesTF.getText().trim().toLowerCase().replaceAll("\\s+", " ");

	        String sql = "SELECT Visits.id, Pets.name AS pet_name, Visits.visit_date, Visits.diagnosis, Owners.name AS owner_name " +
	                     "FROM Visits " +
	                     "JOIN Pets ON Visits.pet_id = Pets.id " +
	                     "JOIN Owners ON Pets.owner_id = Owners.id WHERE 1=1";

	        List<String> parameters = new ArrayList<>();
	        if (visitDateInput != null) {
	            sql += " AND Visits.visit_date = ?";
	            parameters.add(visitDateInput.toString());
	        }
	        if (!speciesInput.isEmpty()) {
	            String[] words = speciesInput.split(" ");
	            for (String word : words) {
	                sql += " AND LOWER(Pets.species) LIKE ?";
	                parameters.add("%" + word + "%");
	            }
	        }

	        try {
	            state = conn.prepareStatement(sql);
	            for (int i = 0; i < parameters.size(); i++) {
	                state.setString(i + 1, parameters.get(i));
	            }
	            ResultSet rs = state.executeQuery();
	            loadSearchToTable(rs); 
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Error during visit search.", "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}
	
	private void loadSearchToTable(ResultSet rs) {
	    try {
	        MyModel model = new MyModel(rs);
	        searchTable.setModel(model);
	    } catch (Exception e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "Error loading search results", "Database Error",
	                JOptionPane.ERROR_MESSAGE);
	    }
	}

	//REFRESH TABLE----------------------------------------------------------------------
	
	private void refreshAllTables() {
		try {
			state = conn.prepareStatement("SELECT * FROM Owners");
			result = state.executeQuery();
			OwnTable.setModel(new MyModel(result));

			String petsWithOwnerName = 
				"SELECT p.id, p.name, p.species, p.birth_date, o.name AS owner_name " +
				"FROM Pets p " +
				"JOIN Owners o ON p.owner_id = o.id";
			state = conn.prepareStatement(petsWithOwnerName);
			result = state.executeQuery();
			petTable.setModel(new MyModel(result));

			state = conn.prepareStatement("select Visits.id, Pets.name as pet_name, Visits.visit_date, Visits.diagnosis,  Owners.name as owner_name "
					+ "from Visits join Pets on Visits.pet_id = Pets.id "
					+ "join Owners on Pets.owner_id = Owners.id");
			result = state.executeQuery();
			VisTable.setModel(new MyModel(result));
			searchTable.setModel(new DefaultTableModel());
			loadOwnersToComboBox();
			loadPetsToComboBox();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Failed to refresh tables.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	class RefreshAllTablesAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			refreshAllTables();
		}
	}
}
