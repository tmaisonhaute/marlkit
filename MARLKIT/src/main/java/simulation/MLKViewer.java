package simulation;

import madkit.simulation.viewer.Viewer2D;

/**
 * Abstract base class for 2D visualization of MARLKIT simulations.
 * <p>
 * Extends the MaDKit {@link Viewer2D}.
 * </p>
 *
 * @see Viewer2D
 * @see MLKModel
 * @see MLKScheduler
 */
public abstract class MLKViewer extends Viewer2D {
	
	
	@Override
	protected void onActivation() {
		super.onActivation();
	}
	
	
}
