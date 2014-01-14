/**
 * Copyright (c) 2011-2012 Evolutionary Design and Optimization Group
 * 
 * Licensed under the MIT License.
 * 
 * See the "LICENSE" file for a copy of the license.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  
 *
 */
package fitness;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class which provides Data interface with target values scaled to the range
 * [0, 1].
 * 
 * @author Owen Derby
 * 
 */
public abstract class ScaledData implements Data {
	/**
	 * the datapoints (input values) to evaluate individual's fitness on.
	 */
	protected final List<List<Double>> fitnessCases;
	/**
	 * the datapoints (output values) to compare individual's output against to
	 * determine their fitness
	 */
	protected final List<Double> target;
	/**
	 * In addition, keep a copy of the target which is scaled to be in the range
	 * [0, 1] This is from Vladislavleva, to "allow the GP to focus on finding
	 * the structure of the solution, instead of the scale"
	 */
	protected final List<Double> scaled_target;
	private Double target_min;
	private Double target_max;

	public ScaledData() {
		fitnessCases = new CopyOnWriteArrayList<List<Double>>();
		this.target = new CopyOnWriteArrayList<Double>();
		this.scaled_target = new CopyOnWriteArrayList<Double>();
		target_min = null;
		target_max = null;
	}

	protected void addTargetValue(Double val) {
		this.target.add(val);
		if (target_min == null || val < target_min) {
			target_min = val;
		}
		if (target_max == null || val > target_max) {
			target_max = val;
		}
	}

	/**
	 * Call this after you've added all target values to target
	 */
	protected void scaleTarget() {
		scaled_target.clear();
		double range = target_max - target_min;
		for (int i = 0; i < this.target.size(); i++) {
			Double val = (this.target.get(i) - target_min) / range;
			this.scaled_target.add(val);
		}
	}

	@Override
	public List<List<Double>> getInputValues() {
		return fitnessCases;
	}

	@Override
	public List<Double> getTargetValues() {
		return target;
	}

	@Override
	public List<Double> getScaledTargetValues() {
		return scaled_target;
	}

	@Override
	public Double getTargetMax() {
		return target_max;
	}

	@Override
	public Double getTargetMin() {
		return target_min;
	}
}