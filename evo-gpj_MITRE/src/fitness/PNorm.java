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

/**
 * Abstract class specifying the normalization object, to be used in normalizing
 * fitness evaluations.
 * 
 * @author Owen Derby
 * 
 */
public abstract class PNorm {

	protected Double error;
	// num errors incorporated
	protected int num_cases;

	public abstract Double calcErr();

	public void addErr(Double e) {
		num_cases++;
	}

	public PNorm() {
		clear();
	}

	public void clear() {
		error = 0.0;
		num_cases = 0;
	}

	public abstract int getP();
	
	public static PNorm newNorm(int pNorm) {
		if (pNorm == 1) {
			return new L1Norm();
		} else if (pNorm == -1) {
			return new InfNorm();
		} else {
			return new LPNorm(pNorm);
		}
	}

}