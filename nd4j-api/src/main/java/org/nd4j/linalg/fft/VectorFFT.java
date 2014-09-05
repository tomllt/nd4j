package org.nd4j.linalg.fft;

import com.google.common.base.Function;
import org.apache.commons.math3.util.FastMath;

import org.nd4j.linalg.api.complex.IComplexNDArray;
import org.nd4j.linalg.api.complex.IComplexNumber;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.ArrayOps;
import org.nd4j.linalg.ops.transforms.Exp;
import org.nd4j.linalg.util.ComplexNDArrayUtil;


/**
 * Encapsulated vector operation
 *
 * @author Adam Gibson
 */
public class VectorFFT implements Function<IComplexNDArray,IComplexNDArray> {
    private int n;
    private int originalN = -1;
    /**
     * Create a vector fft operation.
     * If initialized with  a nonzero number, this will
     * find the next power of 2 for the element and truncate the
     * return matrix to the original n
     * @param n
     */
    public VectorFFT(int n) {
        this.n = n;
    }

    @Override
    public IComplexNDArray apply(IComplexNDArray ndArray) {
        double len = n;

        int desiredElementsAlongDimension = ndArray.length();

        if(len > desiredElementsAlongDimension) {
            ndArray = ComplexNDArrayUtil.padWithZeros(ndArray, new int[]{n});
        }

        else if(len < desiredElementsAlongDimension) {
            ndArray = ComplexNDArrayUtil.truncate(ndArray, n, 0);
        }


        IComplexNumber c2 = Nd4j.createDouble(0, -2).muli(FastMath.PI);
        //row vector
        //IComplexNDArray n = IComplexNDArray.wrap(MatrixUtil.arange(0d, this.n));
        INDArray n = Nd4j.arange(0, this.n);

        //column vector
        INDArray k = n.reshape(new int[]{n.length(),1});
        IComplexNDArray M = Nd4j.createComplex(k.mmul(n)
                .muli(Nd4j.scalar(c2)).divi(Nd4j.scalar(len)));
        new ArrayOps().from(M).op(Exp.class).build().exec();



        IComplexNDArray reshaped = ndArray.reshape(new int[]{ndArray.length()});
        IComplexNDArray matrix = reshaped.mmul(M);
        if(originalN > 0) {
            matrix = ComplexNDArrayUtil.truncate(matrix, originalN, 0);

        }

        return matrix;
    }


}
