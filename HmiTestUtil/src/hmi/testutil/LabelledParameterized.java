/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package hmi.testutil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.Parameterized;

/**
 * Parameterized with understandable test names, to be provided as the first element in the object array.
 * code from uk.ac.lkl.common.util.testing.LabelledParameterized 
 * @author Darren Pearce
 */
public class LabelledParameterized extends Parameterized {

    private List<String> labels;

    private Description labelledDescription;

    public LabelledParameterized(Class<?> cl) throws Throwable {
        super(cl);
        initialiseLabels();
        generateLabelledDescription();
    }

    private void initialiseLabels() throws Exception {
        Collection<Object[]> parameterArrays = getParameterArrays();
        labels = new ArrayList<String>();
        for (Object[] parameterArray : parameterArrays) {
            String label = parameterArray[0].toString();
            labels.add(label);
        }

    }

    private Collection<Object[]> getParameterArrays() throws Exception {
        Method testClassMethod = getDeclaredMethod(this.getClass(),
                "getTestClass");
        Class<?> returnType = testClassMethod.getReturnType();
        if (returnType == Class.class)
            return getParameterArrays4_3();
        else
            return getParameterArrays4_4();
    }

    private Collection<Object[]> getParameterArrays4_3() throws Exception {
        Object[][] methodCalls = new Object[][] { new Object[] { "getTestClass" } };
        Class<?> cl = invokeMethodChain(this, methodCalls);
        Method[] methods = cl.getMethods();

        Method parametersMethod = null;
        for (Method method : methods) {
            boolean providesParameters = method
                    .isAnnotationPresent(Parameters.class);
            if (!providesParameters)
                continue;

            if (parametersMethod != null)
                throw new Exception(
                        "Only one method should be annotated with @Labels");

            parametersMethod = method;
        }

        if (parametersMethod == null)
            throw new Exception("No @Parameters method found");

        @SuppressWarnings("unchecked")
        Collection<Object[]> parameterArrays = (Collection<Object[]>) parametersMethod.invoke(null);
        return parameterArrays;

    }

    private Collection<Object[]> getParameterArrays4_4() throws Exception {
        Object[][] methodCalls = new Object[][] {
                new Object[] { "getTestClass" },
                new Object[] { "getAnnotatedMethods", Class.class,
                        Parameters.class },
                new Object[] { "get", int.class, 0 },
                // use array type for varargs (equivalent (almost))
                new Object[] { "invokeExplosively", Object.class, null,
                        Object[].class, new Object[] {} } };
        Collection<Object[]> parameterArrays = invokeMethodChain(this,
                methodCalls);
        return parameterArrays;
    }

    @SuppressWarnings("unchecked")
    private <T> T invokeMethodChain(Object object, Object[][] methodCalls)
            throws Exception {
        for (Object[] methodCall : methodCalls) {
            String methodName = (String) methodCall[0];
            int parameterCount = (methodCall.length - 1) / 2;
            Class<?>[] classes = new Class<?>[parameterCount];
            Object[] arguments = new Object[parameterCount];
            for (int i = 1; i < methodCall.length; i += 2) {
                Class<?> cl = (Class<?>) methodCall[i];
                Object argument = methodCall[i + 1];
                int index = (i - 1) / 2; // messy!
                classes[index] = cl;
                arguments[index] = argument;
            }
            Method method = getDeclaredMethod(object.getClass(), methodName,
                    classes);
            object = method.invoke(object, arguments);
        }
        return (T) object;
    }

    // iterates through super-classes until found. Throws NoSuchMethodException
    // if not
    private Method getDeclaredMethod(Class<?> cl, String methodName,
            Class<?>... parameterTypes) throws NoSuchMethodException {
        do {
            try {
                Method method = cl
                        .getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (NoSuchMethodException e) {
                // do nothing - just fall through to the below
            }
            cl = cl.getSuperclass();
        } while (cl != null);
        throw new NoSuchMethodException("Method " + methodName
                + "() not found in hierarchy");
    }

    private void generateLabelledDescription() throws Exception {
        Description originalDescription = super.getDescription();
        labelledDescription = Description
                .createSuiteDescription(originalDescription.getDisplayName());
        ArrayList<Description> childDescriptions = originalDescription
                .getChildren();
        int childCount = childDescriptions.size();
        if (childCount != labels.size())
            throw new Exception(
                    "Number of labels and number of parameters must match.");

        for (int i = 0; i < childDescriptions.size(); i++) {
            Description childDescription = childDescriptions.get(i);
            String label = labels.get(i);
            Description newDescription = Description
                    .createSuiteDescription(label);
            ArrayList<Description> grandChildren = childDescription
                    .getChildren();
            for (Description grandChild : grandChildren)
                newDescription.addChild(grandChild);
            labelledDescription.addChild(newDescription);
        }
    }

    @Override
    public Description getDescription() {
        return labelledDescription;
    }

}
