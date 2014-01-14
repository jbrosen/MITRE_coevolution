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

public class LPNorm extends PNorm {

	private final int pow;

	public LPNorm(int pow) {
		super();
		this.pow = pow;
	}

	@Override
	public Double calcErr() {
		// normalize error to account for number of training samples (ie produce
		// an error which is independent of test-case size)
		return Math.pow(error / num_cases, 1 / (double) pow);
	}

	@Override
	public void addErr(Double e) {
		super.addErr(e);
		error += Math.pow(Math.abs(e), pow);
	}

	@Override
	public int getP() {
		return pow;
	}

}
