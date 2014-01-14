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
package phenotype;

import java.util.ArrayList;
import java.util.List;

/**
 * Phenotype class for symbolic regression problems. Tracks the y values
 * predicted by a given individual (its "dataValues") as well as the error of
 * that prediction. Also tracks min/max prediction values to make scaling
 * predictions easier.
 * 
 * @author Owen Derby
 */
public class SRPhenotype extends Phenotype {

	//
	private List<Double> dataValues;
	private Double error;
	public Double min, max;

	public SRPhenotype() {
		dataValues = new ArrayList<Double>();
		error = null;
		min = null;
		max = null;
	}

	public Double getDataValue(int i) {
		return dataValues.get(i);
	}

	public void setDataValue(Double dataValue) {
		this.dataValues.add(dataValue);
		if (min == null || dataValue < min) {
			min = dataValue;
		}
		if (max == null || dataValue > max) {
			max = dataValue;
		}

	}

	@Override
	public String toString() {
		return dataValues.toString();
	}

	public void setError(Double error) {
		this.error = error;
	}

	public Double getError() {
		return error;
	}

	@Override
	public Phenotype copy() {
		SRPhenotype s = new SRPhenotype();
		s.dataValues = new ArrayList<Double>();
		s.dataValues.addAll(this.dataValues);
		s.setError(this.error);
		s.min = this.min;
		s.max = this.max;
		return s;
	}
}
