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
package genotype;

import java.util.ArrayList;
import java.io.*;
import jscheme.JScheme;

public class EvalJScheme {

	public EvalJScheme() {
	}

	public static String readFunctionString(String filename) {
		String matExpr = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			matExpr = in.readLine();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Convert from infix to S-expression
		matExpr = matExpr.replace("sin(", "(sin ");
		matExpr = matExpr.replace("cos(", "(cos ");
		matExpr = matExpr.replace("times(", "(times ");
		matExpr = matExpr.replace("plus(", "(plus ");
		matExpr = matExpr.replace("minus(", "(minus ");
		matExpr = matExpr.replace("mylog(", "(mylog ");
		matExpr = matExpr.replace("mydivide(", "(mydivide ");
		matExpr = matExpr.replace("exp(", "(exp ");
		matExpr = matExpr.replace(',', ' ');
		return matExpr;
	}

	public static ArrayList<Double> readInputData(String filename) {
		ArrayList<Double> data = new ArrayList<Double>();
		String line;
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				data.add(Double.parseDouble(line));
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {

		// Set up JScheme with some function aliases and protected functions
		JScheme js = new JScheme();
		js.eval("(define times *)");
		js.eval("(define plus +)");
		js.eval("(define minus -)");
		js.eval("(define mydivide (lambda (a b) (if (< (abs b) 0.000001) a (/ a b))))");
		js.eval("(define mylog (lambda (a) (if (< (abs a) 0.000001) a (log (abs a)))))");

		// Read the function string
		String matExpr = readFunctionString("../matlab/treeStr.txt");

		// Time many JScheme evaluations of the function definition
		long start = System.currentTimeMillis();
		String ind = "(define f (lambda (X1) " + matExpr + "))";
		int nevals = 1000;
		for (int i = 0; i < nevals; i++) {
			js.eval(ind);
		}
		long end = System.currentTimeMillis();
		long elapsedTimeMillis = end - start;
		float elapsed = elapsedTimeMillis / 1000F;
		System.out.println("Java compile function " + nevals + " times: "
				+ elapsed);

		// Read input data
		String line;
		ArrayList<Double> data = new ArrayList<Double>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"../matlab/input.txt"));
			while ((line = in.readLine()) != null) {
				data.add(Double.parseDouble(line));
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int npts = data.size();

		// Time many JScheme evaluations of the function invocation
		start = System.currentTimeMillis();
		for (Double d : data) {
			js.eval("(f " + d + ")");
		}
		end = System.currentTimeMillis();
		elapsedTimeMillis = end - start;
		elapsed = elapsedTimeMillis / 1000F;
		System.out.println("Java run eval'd function " + npts + " times: "
				+ elapsed);
	}

}