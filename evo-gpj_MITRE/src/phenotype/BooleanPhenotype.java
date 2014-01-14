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

public class BooleanPhenotype extends Phenotype {

	private List<Boolean> dataValues;

	public BooleanPhenotype() {
		dataValues = new ArrayList<Boolean>();
	}

	public Boolean getDataValue(int i) {
		return dataValues.get(i);
	}

	public void setDataValue(Boolean dataValue) {
		this.dataValues.add(dataValue);
	}

	public int size() {
		return dataValues.size();
	}

	@Override
	public String toString() {
		return dataValues.toString();
	}

	@Override
	public Phenotype copy() {
		BooleanPhenotype b = new BooleanPhenotype();
		b.dataValues = new ArrayList<Boolean>();
		b.dataValues.addAll(this.dataValues);
		return b;
	}
}
