package morlok8k.MinecraftLandGenerator.GUI;

import morlok8k.MinecraftLandGenerator.Time;
import morlok8k.MinecraftLandGenerator.var;

public class Start implements Runnable {

	static void start_GUI() {

		// TODO: add additional start code

		var.runningServerGUI = true;

		MLG_GUI.btnStop.setEnabled(true);
		MLG_GUI.btnStart.setEnabled(false);

		MLG_GUI.mntmStop.setEnabled(true);
		MLG_GUI.mntmStart.setEnabled(false);

		MLG_GUI.SizeSetEnable(false);
		MLG_GUI.CenterPointSetEnable(false);

		MLG_GUI.rdbtnSizeSquarify.setEnabled(false);
		MLG_GUI.rdbtnSizeCustomSize.setEnabled(false);

		MLG_GUI.rdbtnAlignRegions.setEnabled(false);
		MLG_GUI.rdbtnAlignChunks.setEnabled(false);

		MLG_GUI.rdbtnCenterSpawnPoint.setEnabled(false);
		MLG_GUI.rdbtnCenterOther.setEnabled(false);

		MLG_GUI.pgbCurPer.setIndeterminate(true);
		MLG_GUI.pgbTotPer.setIndeterminate(true);

		//TODO: add values from textboxes and radio buttons to the actual vars.

		if (MLG_GUI.rdbtnAlignRegions.isSelected()) {
			var.useChunks = false;
		} else {
			var.useChunks = true;
		}

		if (MLG_GUI.rdbtnSizeCustomSize.isSelected()) {
			var.xRange = Integer.parseInt(MLG_GUI.txtSizeX.getText().trim());
			var.zRange = Integer.parseInt(MLG_GUI.txtSizeZ.getText().trim());
		} else {
			var.xRange = 1000;		// Umm...  This code shouldn't run at this point in time...
			var.zRange = 1000;

			//TODO: add squarifying code here.

		}

		if (MLG_GUI.rdbtnCenterOther.isSelected()) {

			var.xOffset = Integer.parseInt(MLG_GUI.txtCPX.getText().trim());
			var.zOffset = Integer.parseInt(MLG_GUI.txtCPZ.getText().trim());

		} else {

			var.xOffset = 0;
			var.zOffset = 0;
			// TODO: get spawnpoint

			MLG_GUI.txtCPX.setText(var.xOffset.toString());
			MLG_GUI.txtCPZ.setText(var.zOffset.toString());

		}

		while (var.stoppingServerGUI == false) {
			// this is where we run the server loops!
			MLG_GUI.frmMLG_GUI.repaint();
			Time.waitTenSec(true);
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		start_GUI();
	}

}
