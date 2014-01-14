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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import algorithm.AlgorithmBase;

public class CSVData extends ScaledData {
	/**
	 * Parse given csvfile into set of input and target values.
	 * 
	 * @param csvfile
	 *            file of comma-separated values, last value is target value
	 */
	public CSVData(String csvfile) {
		super();
		BufferedReader f;
		try {
			f = new BufferedReader(new FileReader(csvfile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		String[] token;
		try {
			/**
			 * Keep track of max/min target values (Vladislavleva suggested) to
			 * perform approximate scaling.
			 */
			while (f.ready()) {
				token = f.readLine().split(",");
				List<Double> t = new ArrayList<Double>();
				for (int i = 0; i < token.length - 1; i++) {
					t.add(Double.valueOf(token[i]));
				}
				this.fitnessCases.add(t);
				Double val = Double.valueOf(token[token.length - 1]);
				this.addTargetValue(val);
			}
			this.scaleTarget();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (AlgorithmBase.VERBOSE)
			System.out.println("We have " + this.fitnessCases.size()
					+ " fitness cases and " + this.target.size() + " values");
	}

}
