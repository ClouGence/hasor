/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.classfmt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.env.*;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ClassFileReader extends ClassFileStruct implements IBinaryType {

	private int accessFlags;
	private char[] classFileName;
	private char[] className;
	private int classNameIndex;
	private int constantPoolCount;
	private AnnotationInfo[] annotations;
	private FieldInfo[] fields;
	private int fieldsCount;

	// initialized in case the .class file is a nested type
	private InnerClassInfo innerInfo;
	private int innerInfoIndex;
	private InnerClassInfo[] innerInfos;
	private char[][] interfaceNames;
	private int interfacesCount;
	private MethodInfo[] methods;
	private int methodsCount;
	private char[] signature;
	private char[] sourceName;
	private char[] sourceFileName;
	private char[] superclassName;
	private long tagBits;
	private long version;
	private char[] enclosingTypeName;
	private char[][][] missingTypeNames;
	private int enclosingNameAndTypeIndex;
	private char[] enclosingMethod;

private static String printTypeModifiers(int modifiers) {
	java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
	java.io.PrintWriter print = new java.io.PrintWriter(out);

	if ((modifiers & ClassFileConstants.AccPublic) != 0) print.print("public "); //$NON-NLS-1$
	if ((modifiers & ClassFileConstants.AccPrivate) != 0) print.print("private "); //$NON-NLS-1$
	if ((modifiers & ClassFileConstants.AccFinal) != 0) print.print("final "); //$NON-NLS-1$
	if ((modifiers & ClassFileConstants.AccSuper) != 0) print.print("super "); //$NON-NLS-1$
	if ((modifiers & ClassFileConstants.AccInterface) != 0) print.print("interface "); //$NON-NLS-1$
	if ((modifiers & ClassFileConstants.AccAbstract) != 0) print.print("abstract "); //$NON-NLS-1$
	print.flush();
	return out.toString();
}

public static ClassFileReader read(File file) throws ClassFormatException, IOException {
	return read(file, false);
}

public static ClassFileReader read(File file, boolean fullyInitialize) throws ClassFormatException, IOException {
	byte classFileBytes[] = Util.getFileByteContent(file);
	ClassFileReader classFileReader = new ClassFileReader(classFileBytes, file.getAbsolutePath().toCharArray());
	if (fullyInitialize) {
		classFileReader.initialize();
	}
	return classFileReader;
}

public static ClassFileReader read(InputStream stream, String fileName) throws ClassFormatException, IOException {
	return read(stream, fileName, false);
}

public static ClassFileReader read(InputStream stream, String fileName, boolean fullyInitialize) throws ClassFormatException, IOException {
	byte classFileBytes[] = Util.getInputStreamAsByteArray(stream, -1);
	ClassFileReader classFileReader = new ClassFileReader(classFileBytes, fileName.toCharArray());
	if (fullyInitialize) {
		classFileReader.initialize();
	}
	return classFileReader;
}

public static ClassFileReader read(
	java.util.zip.ZipFile zip,
	String filename)
	throws ClassFormatException, java.io.IOException {
		return read(zip, filename, false);
}

public static ClassFileReader read(
	java.util.zip.ZipFile zip,
	String filename,
	boolean fullyInitialize)
	throws ClassFormatException, java.io.IOException {
	java.util.zip.ZipEntry ze = zip.getEntry(filename);
	if (ze == null)
		return null;
	byte classFileBytes[] = Util.getZipEntryByteContent(ze, zip);
	ClassFileReader classFileReader = new ClassFileReader(classFileBytes, filename.toCharArray());
	if (fullyInitialize) {
		classFileReader.initialize();
	}
	return classFileReader;
}

public static ClassFileReader read(String fileName) throws ClassFormatException, java.io.IOException {
	return read(fileName, false);
}

public static ClassFileReader read(String fileName, boolean fullyInitialize) throws ClassFormatException, java.io.IOException {
	return read(new File(fileName), fullyInitialize);
}

/**
 * @param classFileBytes Actual bytes of a .class file
 * @param fileName	Actual name of the file that contains the bytes, can be null
 *
 * @exception ClassFormatException
 */
public ClassFileReader(byte classFileBytes[], char[] fileName) throws ClassFormatException {
	this(classFileBytes, fileName, false);
}

/**
 * @param classFileBytes byte[]
 * 		Actual bytes of a .class file
 *
 * @param fileName char[]
 * 		Actual name of the file that contains the bytes, can be null
 *
 * @param fullyInitialize boolean
 * 		Flag to fully initialize the new object
 * @exception ClassFormatException
 */
public ClassFileReader(byte[] classFileBytes, char[] fileName, boolean fullyInitialize) throws ClassFormatException {
	// This method looks ugly but is actually quite simple, the constantPool is constructed
	// in 3 passes.  All non-primitive constant pool members that usually refer to other members
	// by index are tweaked to have their value in inst vars, this minor cost at read-time makes
	// all subsequent uses of the constant pool element faster.
	super(classFileBytes, null, 0);
	this.classFileName = fileName;
	int readOffset = 10;
	try {
		this.version = ((long)u2At(6) << 16) + u2At(4); // major<<16 + minor
		this.constantPoolCount = u2At(8);
		// Pass #1 - Fill in all primitive constants
		this.constantPoolOffsets = new int[this.constantPoolCount];
		for (int i = 1; i < this.constantPoolCount; i++) {
			int tag = u1At(readOffset);
			switch (tag) {
				case ClassFileConstants.Utf8Tag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += u2At(readOffset + 1);
					readOffset += ClassFileConstants.ConstantUtf8FixedSize;
					break;
				case ClassFileConstants.IntegerTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantIntegerFixedSize;
					break;
				case ClassFileConstants.FloatTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantFloatFixedSize;
					break;
				case ClassFileConstants.LongTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantLongFixedSize;
					i++;
					break;
				case ClassFileConstants.DoubleTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantDoubleFixedSize;
					i++;
					break;
				case ClassFileConstants.ClassTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantClassFixedSize;
					break;
				case ClassFileConstants.StringTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantStringFixedSize;
					break;
				case ClassFileConstants.FieldRefTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantFieldRefFixedSize;
					break;
				case ClassFileConstants.MethodRefTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantMethodRefFixedSize;
					break;
				case ClassFileConstants.InterfaceMethodRefTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantInterfaceMethodRefFixedSize;
					break;
				case ClassFileConstants.NameAndTypeTag :
					this.constantPoolOffsets[i] = readOffset;
					readOffset += ClassFileConstants.ConstantNameAndTypeFixedSize;
			}
		}
		// Read and validate access flags
		this.accessFlags = u2At(readOffset);
		readOffset += 2;

		// Read the classname, use exception handlers to catch bad format
		this.classNameIndex = u2At(readOffset);
		this.className = getConstantClassNameAt(this.classNameIndex);
		readOffset += 2;

		// Read the superclass name, can be null for java.lang.Object
		int superclassNameIndex = u2At(readOffset);
		readOffset += 2;
		// if superclassNameIndex is equals to 0 there is no need to set a value for the
		// field this.superclassName. null is fine.
		if (superclassNameIndex != 0) {
			this.superclassName = getConstantClassNameAt(superclassNameIndex);
		}

		// Read the interfaces, use exception handlers to catch bad format
		this.interfacesCount = u2At(readOffset);
		readOffset += 2;
		if (this.interfacesCount != 0) {
			this.interfaceNames = new char[this.interfacesCount][];
			for (int i = 0; i < this.interfacesCount; i++) {
				this.interfaceNames[i] = getConstantClassNameAt(u2At(readOffset));
				readOffset += 2;
			}
		}
		// Read the fields, use exception handlers to catch bad format
		this.fieldsCount = u2At(readOffset);
		readOffset += 2;
		if (this.fieldsCount != 0) {
			FieldInfo field;
			this.fields = new FieldInfo[this.fieldsCount];
			for (int i = 0; i < this.fieldsCount; i++) {
				field = FieldInfo.createField(this.reference, this.constantPoolOffsets, readOffset);
				this.fields[i] = field;
				readOffset += field.sizeInBytes();
			}
		}
		// Read the methods
		this.methodsCount = u2At(readOffset);
		readOffset += 2;
		if (this.methodsCount != 0) {
			this.methods = new MethodInfo[this.methodsCount];
			boolean isAnnotationType = (this.accessFlags & ClassFileConstants.AccAnnotation) != 0;
			for (int i = 0; i < this.methodsCount; i++) {
				this.methods[i] = isAnnotationType
					? AnnotationMethodInfo.createAnnotationMethod(this.reference, this.constantPoolOffsets, readOffset)
					: MethodInfo.createMethod(this.reference, this.constantPoolOffsets, readOffset);
				readOffset += this.methods[i].sizeInBytes();
			}
		}

		// Read the attributes
		int attributesCount = u2At(readOffset);
		readOffset += 2;

		for (int i = 0; i < attributesCount; i++) {
			int utf8Offset = this.constantPoolOffsets[u2At(readOffset)];
			char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
			if (attributeName.length == 0) {
				readOffset += (6 + u4At(readOffset + 2));
				continue;
			}
			switch(attributeName[0] ) {
				case 'E' :
					if (CharOperation.equals(attributeName, AttributeNamesConstants.EnclosingMethodName)) {
						utf8Offset =
							this.constantPoolOffsets[u2At(this.constantPoolOffsets[u2At(readOffset + 6)] + 1)];
 						this.enclosingTypeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
						this.enclosingNameAndTypeIndex = u2At(readOffset + 8);
					}
					break;
				case 'D' :
					if (CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName)) {
						this.accessFlags |= ClassFileConstants.AccDeprecated;
					}
					break;
				case 'I' :
					if (CharOperation.equals(attributeName, AttributeNamesConstants.InnerClassName)) {
						int innerOffset = readOffset + 6;
						int number_of_classes = u2At(innerOffset);
						if (number_of_classes != 0) {
							innerOffset+= 2;
							this.innerInfos = new InnerClassInfo[number_of_classes];
							for (int j = 0; j < number_of_classes; j++) {
								this.innerInfos[j] =
									new InnerClassInfo(this.reference, this.constantPoolOffsets, innerOffset);
								if (this.classNameIndex == this.innerInfos[j].innerClassNameIndex) {
									this.innerInfo = this.innerInfos[j];
									this.innerInfoIndex = j;
								}
								innerOffset += 8;
							}
							if (this.innerInfo != null) {
								char[] enclosingType = this.innerInfo.getEnclosingTypeName();
								if (enclosingType != null) {
									this.enclosingTypeName = enclosingType;
								}
							}
						}
					} else if (CharOperation.equals(attributeName, AttributeNamesConstants.InconsistentHierarchy)) {
						this.tagBits |= TagBits.HierarchyHasProblems;
					}
					break;
				case 'S' :
					if (attributeName.length > 2) {
						switch(attributeName[1]) {
							case 'o' :
								if (CharOperation.equals(attributeName, AttributeNamesConstants.SourceName)) {
									utf8Offset = this.constantPoolOffsets[u2At(readOffset + 6)];
									this.sourceFileName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
								}
								break;
							case 'y' :
								if (CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) {
									this.accessFlags |= ClassFileConstants.AccSynthetic;
								}
								break;
							case 'i' :
								if (CharOperation.equals(attributeName, AttributeNamesConstants.SignatureName)) {
									utf8Offset = this.constantPoolOffsets[u2At(readOffset + 6)];
									this.signature = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
								}
						}
					}
					break;
				case 'R' :
					if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
						decodeAnnotations(readOffset, true);
					} else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
						decodeAnnotations(readOffset, false);
					}
					break;
				case 'M' :
					if (CharOperation.equals(attributeName, AttributeNamesConstants.MissingTypesName)) {
						// decode the missing types
						int missingTypeOffset = readOffset + 6;
						int numberOfMissingTypes = u2At(missingTypeOffset);
						if (numberOfMissingTypes != 0) {
							this.missingTypeNames = new char[numberOfMissingTypes][][];
							missingTypeOffset += 2;
							for (int j = 0; j < numberOfMissingTypes; j++) {
								utf8Offset = this.constantPoolOffsets[u2At(this.constantPoolOffsets[u2At(missingTypeOffset)] + 1)];
								char[] missingTypeConstantPoolName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
								this.missingTypeNames[j] = CharOperation.splitOn('/', missingTypeConstantPoolName);
								missingTypeOffset += 2;
							}
						}
					}
			}
			readOffset += (6 + u4At(readOffset + 2));
		}
		if (fullyInitialize) {
			initialize();
		}
	} catch(ClassFormatException e) {
		throw e;
	} catch (Exception e) {
		throw new ClassFormatException(
			ClassFormatException.ErrTruncatedInput,
			readOffset);
	}
}

/**
 * Answer the receiver's access flags.  The value of the access_flags
 *	item is a mask of modifiers used with class and interface declarations.
 *  @return int
 */
public int accessFlags() {
	return this.accessFlags;
}

private void decodeAnnotations(int offset, boolean runtimeVisible) {
	int numberOfAnnotations = u2At(offset + 6);
	if (numberOfAnnotations > 0) {
		int readOffset = offset + 8;
		AnnotationInfo[] newInfos = null;
		int newInfoCount = 0;
		for (int i = 0; i < numberOfAnnotations; i++) {
			// With the last parameter being 'false', the data structure will not be flushed out
			AnnotationInfo newInfo = new AnnotationInfo(this.reference, this.constantPoolOffsets, readOffset, runtimeVisible, false);
			readOffset += newInfo.readOffset;
			long standardTagBits = newInfo.standardAnnotationTagBits;
			if (standardTagBits != 0) {
				this.tagBits |= standardTagBits;
			} else {
				if (newInfos == null)
					newInfos = new AnnotationInfo[numberOfAnnotations - i];
				newInfos[newInfoCount++] = newInfo;
			}
		}
		if (newInfos == null)
			return; // nothing to record in this.annotations

		if (this.annotations == null) {
			if (newInfoCount != newInfos.length)
				System.arraycopy(newInfos, 0, newInfos = new AnnotationInfo[newInfoCount], 0, newInfoCount);
			this.annotations = newInfos;
		} else {
			int length = this.annotations.length;
			AnnotationInfo[] temp = new AnnotationInfo[length + newInfoCount];
			System.arraycopy(this.annotations, 0, temp, 0, length);
			System.arraycopy(newInfos, 0, temp, length, newInfoCount);
			this.annotations = temp;
		}
	}
}

/**
 * @return the annotations or null if there is none.
 */
public IBinaryAnnotation[] getAnnotations() {
	return this.annotations;
}

/**
 * Answer the char array that corresponds to the class name of the constant class.
 * constantPoolIndex is the index in the constant pool that is a constant class entry.
 *
 * @param constantPoolIndex int
 * @return char[]
 */
private char[] getConstantClassNameAt(int constantPoolIndex) {
	int utf8Offset = this.constantPoolOffsets[u2At(this.constantPoolOffsets[constantPoolIndex] + 1)];
	return utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
}

/**
 * Answer the int array that corresponds to all the offsets of each entry in the constant pool
 *
 * @return int[]
 */
public int[] getConstantPoolOffsets() {
	return this.constantPoolOffsets;
}

public char[] getEnclosingMethod() {
	if (this.enclosingNameAndTypeIndex <= 0) {
		return null;
	}
	if (this.enclosingMethod == null) {
		// read the name
		StringBuffer buffer = new StringBuffer();
		
		int nameAndTypeOffset = this.constantPoolOffsets[this.enclosingNameAndTypeIndex];
		int utf8Offset = this.constantPoolOffsets[u2At(nameAndTypeOffset + 1)];
		buffer.append(utf8At(utf8Offset + 3, u2At(utf8Offset + 1)));

		utf8Offset = this.constantPoolOffsets[u2At(nameAndTypeOffset + 3)];
		buffer.append(utf8At(utf8Offset + 3, u2At(utf8Offset + 1)));

		this.enclosingMethod = String.valueOf(buffer).toCharArray();
	}
	return this.enclosingMethod;
}

/*
 * Answer the resolved compoundName of the enclosing type
 * or null if the receiver is a top level type.
 */
public char[] getEnclosingTypeName() {
	return this.enclosingTypeName;
}

/**
 * Answer the receiver's this.fields or null if the array is empty.
 * @return org.eclipse.jdt.internal.compiler.api.IBinaryField[]
 */
public IBinaryField[] getFields() {
	return this.fields;
}

/**
 * @see org.eclipse.jdt.internal.compiler.env.IDependent#getFileName()
 */
public char[] getFileName() {
	return this.classFileName;
}

public char[] getGenericSignature() {
	return this.signature;
}

/**
 * Answer the source name if the receiver is a inner type. Return null if it is an anonymous class or if the receiver is a top-level class.
 * e.g.
 * public class A {
 *	public class B {
 *	}
 *	public void foo() {
 *		class C {}
 *	}
 *	public Runnable bar() {
 *		return new Runnable() {
 *			public void run() {}
 *		};
 *	}
 * }
 * It returns {'B'} for the member A$B
 * It returns null for A
 * It returns {'C'} for the local class A$1$C
 * It returns null for the anonymous A$1
 * @return char[]
 */
public char[] getInnerSourceName() {
	if (this.innerInfo != null)
		return this.innerInfo.getSourceName();
	return null;
}

/**
 * Answer the resolved names of the receiver's interfaces in the
 * class file format as specified in section 4.2 of the Java 2 VM spec
 * or null if the array is empty.
 *
 * For example, java.lang.String is java/lang/String.
 * @return char[][]
 */
public char[][] getInterfaceNames() {
	return this.interfaceNames;
}

/**
 * Answer the receiver's nested types or null if the array is empty.
 *
 * This nested type info is extracted from the inner class attributes. Ask the
 * name environment to find a member type using its compound name
 *
 * @return org.eclipse.jdt.internal.compiler.api.IBinaryNestedType[]
 */
public IBinaryNestedType[] getMemberTypes() {
	// we might have some member types of the current type
	if (this.innerInfos == null) return null;

	int length = this.innerInfos.length;
	int startingIndex = this.innerInfo != null ? this.innerInfoIndex + 1 : 0;
	if (length != startingIndex) {
		IBinaryNestedType[] memberTypes =
			new IBinaryNestedType[length - this.innerInfoIndex];
		int memberTypeIndex = 0;
		for (int i = startingIndex; i < length; i++) {
			InnerClassInfo currentInnerInfo = this.innerInfos[i];
			int outerClassNameIdx = currentInnerInfo.outerClassNameIndex;
			int innerNameIndex = currentInnerInfo.innerNameIndex;
			/*
			 * Checking that outerClassNameIDx is different from 0 should be enough to determine if an inner class
			 * attribute entry is a member class, but due to the bug:
			 * http://dev.eclipse.org/bugs/show_bug.cgi?id=14592
			 * we needed to add an extra check. So we check that innerNameIndex is different from 0 as well.
			 *
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=49879
			 * From JavaMail 1.2, the class javax.mail.Folder contains an anonymous class in the
			 * terminateQueue() method for which the inner attribute is boggus.
			 * outerClassNameIdx is not 0, innerNameIndex is not 0, but the sourceName length is 0.
			 * So I added this extra check to filter out this anonymous class from the
			 * member types.
			 */
			if (outerClassNameIdx != 0
				&& innerNameIndex != 0
				&& outerClassNameIdx == this.classNameIndex
				&& currentInnerInfo.getSourceName().length != 0) {
				memberTypes[memberTypeIndex++] = currentInnerInfo;
			}
		}
		if (memberTypeIndex == 0) return null;
		if (memberTypeIndex != memberTypes.length) {
			// we need to resize the memberTypes array. Some local or anonymous classes
			// are present in the current class.
			System.arraycopy(
				memberTypes,
				0,
				(memberTypes = new IBinaryNestedType[memberTypeIndex]),
				0,
				memberTypeIndex);
		}
		return memberTypes;
	}
	return null;
}

/**
 * Answer the receiver's this.methods or null if the array is empty.
 * @return org.eclipse.jdt.internal.compiler.api.env.IBinaryMethod[]
 */
public IBinaryMethod[] getMethods() {
	return this.methods;
}

/*
public static void main(String[] args) throws ClassFormatException, IOException {
	if (args == null || args.length != 1) {
		System.err.println("ClassFileReader <filename>"); //$NON-NLS-1$
		System.exit(1);
	}
	File file = new File(args[0]);
	ClassFileReader reader = read(file, true);
	if (reader.annotations != null) {
		System.err.println();
		for (int i = 0; i < reader.annotations.length; i++)
			System.err.println(reader.annotations[i]);
	}
	System.err.print("class "); //$NON-NLS-1$
	System.err.print(reader.getName());
	char[] superclass = reader.getSuperclassName();
	if (superclass != null) {
		System.err.print(" extends "); //$NON-NLS-1$
		System.err.print(superclass);
	}
	System.err.println();
	char[][] interfaces = reader.getInterfaceNames();
	if (interfaces != null && interfaces.length > 0) {
		System.err.print(" implements "); //$NON-NLS-1$
		for (int i = 0; i < interfaces.length; i++) {
			if (i != 0) System.err.print(", "); //$NON-NLS-1$
			System.err.println(interfaces[i]);
		}
	}
	System.err.println();
	System.err.println('{');
	if (reader.fields != null) {
		for (int i = 0; i < reader.fields.length; i++) {
			System.err.println(reader.fields[i]);
			System.err.println();
		}
	}
	if (reader.methods != null) {
		for (int i = 0; i < reader.methods.length; i++) {
			System.err.println(reader.methods[i]);
			System.err.println();
		}
	}
	System.err.println();
	System.err.println('}');
}
*/
public char[][][] getMissingTypeNames() {
	return this.missingTypeNames;
}

/**
 * Answer an int whose bits are set according the access constants
 * defined by the VM spec.
 * Set the AccDeprecated and AccSynthetic bits if necessary
 * @return int
 */
public int getModifiers() {
	int modifiers;
	if (this.innerInfo != null) {
		modifiers = this.innerInfo.getModifiers()
			| (this.accessFlags & ClassFileConstants.AccDeprecated)
			| (this.accessFlags & ClassFileConstants.AccSynthetic);
	} else {
		modifiers = this.accessFlags;
	}
	return modifiers;
}

/**
 * Answer the resolved name of the type in the
 * class file format as specified in section 4.2 of the Java 2 VM spec.
 *
 * For example, java.lang.String is java/lang/String.
 * @return char[]
 */
public char[] getName() {
	return this.className;
}

public char[] getSourceName() {
	if (this.sourceName != null)
		return this.sourceName;

	char[] name = getInnerSourceName(); // member or local scenario
	if (name == null) {
		name = getName(); // extract from full name
		int start;
		if (isAnonymous()) {
			start = CharOperation.indexOf('$', name, CharOperation.lastIndexOf('/', name) + 1) + 1;
		} else {
			start = CharOperation.lastIndexOf('/', name) + 1;
		}
		if (start > 0) {
			char[] newName = new char[name.length - start];
			System.arraycopy(name, start, newName, 0, newName.length);
			name = newName;
		}
	}
	return this.sourceName = name;
}

/**
 * Answer the resolved name of the receiver's superclass in the
 * class file format as specified in section 4.2 of the Java 2 VM spec
 * or null if it does not have one.
 *
 * For example, java.lang.String is java/lang/String.
 * @return char[]
 */
public char[] getSuperclassName() {
	return this.superclassName;
}

public long getTagBits() {
	return this.tagBits;
}

/**
 * Answer the major/minor version defined in this class file according to the VM spec.
 * as a long: (major<<16)+minor
 * @return the major/minor version found
 */
public long getVersion() {
	return this.version;
}

private boolean hasNonSyntheticFieldChanges(FieldInfo[] currentFieldInfos, FieldInfo[] otherFieldInfos) {
	int length1 = currentFieldInfos == null ? 0 : currentFieldInfos.length;
	int length2 = otherFieldInfos == null ? 0 : otherFieldInfos.length;
	int index1 = 0;
	int index2 = 0;

	end : while (index1 < length1 && index2 < length2) {
		while (currentFieldInfos[index1].isSynthetic()) {
			if (++index1 >= length1) break end;
		}
		while (otherFieldInfos[index2].isSynthetic()) {
			if (++index2 >= length2) break end;
		}
		if (hasStructuralFieldChanges(currentFieldInfos[index1++], otherFieldInfos[index2++]))
			return true;
	}

	while (index1 < length1) {
		if (!currentFieldInfos[index1++].isSynthetic()) return true;
	}
	while (index2 < length2) {
		if (!otherFieldInfos[index2++].isSynthetic()) return true;
	}
	return false;
}

private boolean hasNonSyntheticMethodChanges(MethodInfo[] currentMethodInfos, MethodInfo[] otherMethodInfos) {
	int length1 = currentMethodInfos == null ? 0 : currentMethodInfos.length;
	int length2 = otherMethodInfos == null ? 0 : otherMethodInfos.length;
	int index1 = 0;
	int index2 = 0;

	MethodInfo m;
	end : while (index1 < length1 && index2 < length2) {
		while ((m = currentMethodInfos[index1]).isSynthetic() || m.isClinit()) {
			if (++index1 >= length1) break end;
		}
		while ((m = otherMethodInfos[index2]).isSynthetic() || m.isClinit()) {
			if (++index2 >= length2) break end;
		}
		if (hasStructuralMethodChanges(currentMethodInfos[index1++], otherMethodInfos[index2++]))
			return true;
	}

	while (index1 < length1) {
		if (!((m = currentMethodInfos[index1++]).isSynthetic() || m.isClinit())) return true;
	}
	while (index2 < length2) {
		if (!((m = otherMethodInfos[index2++]).isSynthetic() || m.isClinit())) return true;
	}
	return false;
}

/**
 * Check if the receiver has structural changes compare to the byte array in argument.
 * Structural changes are:
 * - modifiers changes for the class, the this.fields or the this.methods
 * - signature changes for this.fields or this.methods.
 * - changes in the number of this.fields or this.methods
 * - changes for field constants
 * - changes for thrown exceptions
 * - change for the super class or any super interfaces.
 * - changes for member types name or modifiers
 * If any of these changes occurs, the method returns true. false otherwise.
 * The synthetic fields are included and the members are not required to be sorted.
 * @param newBytes the bytes of the .class file we want to compare the receiver to
 * @return boolean Returns true is there is a structural change between the two .class files, false otherwise
 */
public boolean hasStructuralChanges(byte[] newBytes) {
	return hasStructuralChanges(newBytes, true, true);
}

/**
 * Check if the receiver has structural changes compare to the byte array in argument.
 * Structural changes are:
 * - modifiers changes for the class, the this.fields or the this.methods
 * - signature changes for this.fields or this.methods.
 * - changes in the number of this.fields or this.methods
 * - changes for field constants
 * - changes for thrown exceptions
 * - change for the super class or any super interfaces.
 * - changes for member types name or modifiers
 * If any of these changes occurs, the method returns true. false otherwise.
 * @param newBytes the bytes of the .class file we want to compare the receiver to
 * @param orderRequired a boolean indicating whether the members should be sorted or not
 * @param excludesSynthetic a boolean indicating whether the synthetic members should be used in the comparison
 * @return boolean Returns true is there is a structural change between the two .class files, false otherwise
 */
public boolean hasStructuralChanges(byte[] newBytes, boolean orderRequired, boolean excludesSynthetic) {
	try {
		ClassFileReader newClassFile =
			new ClassFileReader(newBytes, this.classFileName);
		// type level comparison
		// modifiers
		if (getModifiers() != newClassFile.getModifiers())
			return true;

		// only consider a portion of the tagbits which indicate a structural change for dependents
		// e.g. @Override change has no influence outside
		long OnlyStructuralTagBits = TagBits.AnnotationTargetMASK // different @Target status ?
			| TagBits.AnnotationDeprecated // different @Deprecated status ?
			| TagBits.AnnotationRetentionMASK // different @Retention status ?
			| TagBits.HierarchyHasProblems; // different hierarchy status ?

		// meta-annotations
		if ((getTagBits() & OnlyStructuralTagBits) != (newClassFile.getTagBits() & OnlyStructuralTagBits))
			return true;
		// annotations
		if (hasStructuralAnnotationChanges(getAnnotations(), newClassFile.getAnnotations()))
			return true;

		// generic signature
		if (!CharOperation.equals(getGenericSignature(), newClassFile.getGenericSignature()))
			return true;
		// superclass
		if (!CharOperation.equals(getSuperclassName(), newClassFile.getSuperclassName()))
			return true;
		// interfaces
		char[][] newInterfacesNames = newClassFile.getInterfaceNames();
		if (this.interfaceNames != newInterfacesNames) { // TypeConstants.NoSuperInterfaces
			int newInterfacesLength = newInterfacesNames == null ? 0 : newInterfacesNames.length;
			if (newInterfacesLength != this.interfacesCount)
				return true;
			for (int i = 0, max = this.interfacesCount; i < max; i++)
				if (!CharOperation.equals(this.interfaceNames[i], newInterfacesNames[i]))
					return true;
		}

		// member types
		IBinaryNestedType[] currentMemberTypes = getMemberTypes();
		IBinaryNestedType[] otherMemberTypes = newClassFile.getMemberTypes();
		if (currentMemberTypes != otherMemberTypes) { // TypeConstants.NoMemberTypes
			int currentMemberTypeLength = currentMemberTypes == null ? 0 : currentMemberTypes.length;
			int otherMemberTypeLength = otherMemberTypes == null ? 0 : otherMemberTypes.length;
			if (currentMemberTypeLength != otherMemberTypeLength)
				return true;
			for (int i = 0; i < currentMemberTypeLength; i++)
				if (!CharOperation.equals(currentMemberTypes[i].getName(), otherMemberTypes[i].getName())
					|| currentMemberTypes[i].getModifiers() != otherMemberTypes[i].getModifiers())
						return true;
		}

		// fields
		FieldInfo[] otherFieldInfos = (FieldInfo[]) newClassFile.getFields();
		int otherFieldInfosLength = otherFieldInfos == null ? 0 : otherFieldInfos.length;
		boolean compareFields = true;
		if (this.fieldsCount == otherFieldInfosLength) {
			int i = 0;
			for (; i < this.fieldsCount; i++)
				if (hasStructuralFieldChanges(this.fields[i], otherFieldInfos[i])) break;
			if ((compareFields = i != this.fieldsCount) && !orderRequired && !excludesSynthetic)
				return true;
		}
		if (compareFields) {
			if (this.fieldsCount != otherFieldInfosLength && !excludesSynthetic)
				return true;
			if (orderRequired) {
				if (this.fieldsCount != 0)
					Arrays.sort(this.fields);
				if (otherFieldInfosLength != 0)
					Arrays.sort(otherFieldInfos);
			}
			if (excludesSynthetic) {
				if (hasNonSyntheticFieldChanges(this.fields, otherFieldInfos))
					return true;
			} else {
				for (int i = 0; i < this.fieldsCount; i++)
					if (hasStructuralFieldChanges(this.fields[i], otherFieldInfos[i]))
						return true;
			}
		}

		// methods
		MethodInfo[] otherMethodInfos = (MethodInfo[]) newClassFile.getMethods();
		int otherMethodInfosLength = otherMethodInfos == null ? 0 : otherMethodInfos.length;
		boolean compareMethods = true;
		if (this.methodsCount == otherMethodInfosLength) {
			int i = 0;
			for (; i < this.methodsCount; i++)
				if (hasStructuralMethodChanges(this.methods[i], otherMethodInfos[i])) break;
			if ((compareMethods = i != this.methodsCount) && !orderRequired && !excludesSynthetic)
				return true;
		}
		if (compareMethods) {
			if (this.methodsCount != otherMethodInfosLength && !excludesSynthetic)
				return true;
			if (orderRequired) {
				if (this.methodsCount != 0)
					Arrays.sort(this.methods);
				if (otherMethodInfosLength != 0)
					Arrays.sort(otherMethodInfos);
			}
			if (excludesSynthetic) {
				if (hasNonSyntheticMethodChanges(this.methods, otherMethodInfos))
					return true;
			} else {
				for (int i = 0; i < this.methodsCount; i++)
					if (hasStructuralMethodChanges(this.methods[i], otherMethodInfos[i]))
						return true;
			}
		}

		// missing types
		char[][][] missingTypes = getMissingTypeNames();
		char[][][] newMissingTypes = newClassFile.getMissingTypeNames();
		if (missingTypes != null) {
			if (newMissingTypes == null) {
				return true;
			}
			int length = missingTypes.length;
			if (length != newMissingTypes.length) {
				return true;
			}
			for (int i = 0; i < length; i++) {
				if (!CharOperation.equals(missingTypes[i], newMissingTypes[i])) {
					return true;
				}
			}
		} else if (newMissingTypes != null) {
			return true;
		}
		return false;
	} catch (ClassFormatException e) {
		return true;
	}
}

private boolean hasStructuralAnnotationChanges(IBinaryAnnotation[] currentAnnotations, IBinaryAnnotation[] otherAnnotations) {
	if (currentAnnotations == otherAnnotations)
		return false;

	int currentAnnotationsLength = currentAnnotations == null ? 0 : currentAnnotations.length;
	int otherAnnotationsLength = otherAnnotations == null ? 0 : otherAnnotations.length;
	if (currentAnnotationsLength != otherAnnotationsLength)
		return true;
	for (int i = 0; i < currentAnnotationsLength; i++) {
		if (!CharOperation.equals(currentAnnotations[i].getTypeName(), otherAnnotations[i].getTypeName()))
			return true;
		IBinaryElementValuePair[] currentPairs = currentAnnotations[i].getElementValuePairs();
		IBinaryElementValuePair[] otherPairs = otherAnnotations[i].getElementValuePairs();
		int currentPairsLength = currentPairs == null ? 0 : currentPairs.length;
		int otherPairsLength = otherPairs == null ? 0 : otherPairs.length;
		if (currentPairsLength != otherPairsLength)
			return true;
		for (int j = 0; j < currentPairsLength; j++) {
			if (!CharOperation.equals(currentPairs[j].getName(), otherPairs[j].getName()))
				return true;
			final Object value = currentPairs[j].getValue();
			final Object value2 = otherPairs[j].getValue();
			if (value instanceof Object[]) {
				Object[] currentValues = (Object[]) value;
				if (value2 instanceof Object[]) {
					Object[] currentValues2 = (Object[]) value2;
					final int length = currentValues.length;
					if (length != currentValues2.length) {
						return true;
					}
					for (int n = 0; n < length; n++) {
						if (!currentValues[n].equals(currentValues2[n])) {
							return true;
						}
					}
					return false;
				}
				return true;
			} else if (!value.equals(value2)) {
				return true;
			}
		}
	}
	return false;
}

private boolean hasStructuralFieldChanges(FieldInfo currentFieldInfo, FieldInfo otherFieldInfo) {
	// generic signature
	if (!CharOperation.equals(currentFieldInfo.getGenericSignature(), otherFieldInfo.getGenericSignature()))
		return true;
	if (currentFieldInfo.getModifiers() != otherFieldInfo.getModifiers())
		return true;
	if ((currentFieldInfo.getTagBits() & TagBits.AnnotationDeprecated) != (otherFieldInfo.getTagBits() & TagBits.AnnotationDeprecated))
		return true;
	if (hasStructuralAnnotationChanges(currentFieldInfo.getAnnotations(), otherFieldInfo.getAnnotations()))
		return true;
	if (!CharOperation.equals(currentFieldInfo.getName(), otherFieldInfo.getName()))
		return true;
	if (!CharOperation.equals(currentFieldInfo.getTypeName(), otherFieldInfo.getTypeName()))
		return true;
	if (currentFieldInfo.hasConstant() != otherFieldInfo.hasConstant())
		return true;
	if (currentFieldInfo.hasConstant()) {
		Constant currentConstant = currentFieldInfo.getConstant();
		Constant otherConstant = otherFieldInfo.getConstant();
		if (currentConstant.typeID() != otherConstant.typeID())
			return true;
		if (!currentConstant.getClass().equals(otherConstant.getClass()))
			return true;
		switch (currentConstant.typeID()) {
			case TypeIds.T_int :
				return currentConstant.intValue() != otherConstant.intValue();
			case TypeIds.T_byte :
				return currentConstant.byteValue() != otherConstant.byteValue();
			case TypeIds.T_short :
				return currentConstant.shortValue() != otherConstant.shortValue();
			case TypeIds.T_char :
				return currentConstant.charValue() != otherConstant.charValue();
			case TypeIds.T_long :
				return currentConstant.longValue() != otherConstant.longValue();
			case TypeIds.T_float :
				return currentConstant.floatValue() != otherConstant.floatValue();
			case TypeIds.T_double :
				return currentConstant.doubleValue() != otherConstant.doubleValue();
			case TypeIds.T_boolean :
				return currentConstant.booleanValue() != otherConstant.booleanValue();
			case TypeIds.T_JavaLangString :
				return !currentConstant.stringValue().equals(otherConstant.stringValue());
		}
	}
	return false;
}

private boolean hasStructuralMethodChanges(MethodInfo currentMethodInfo, MethodInfo otherMethodInfo) {
	// generic signature
	if (!CharOperation.equals(currentMethodInfo.getGenericSignature(), otherMethodInfo.getGenericSignature()))
		return true;
	if (currentMethodInfo.getModifiers() != otherMethodInfo.getModifiers())
		return true;
	if ((currentMethodInfo.getTagBits() & TagBits.AnnotationDeprecated) != (otherMethodInfo.getTagBits() & TagBits.AnnotationDeprecated))
		return true;
	if (hasStructuralAnnotationChanges(currentMethodInfo.getAnnotations(), otherMethodInfo.getAnnotations()))
		return true;
	if (!CharOperation.equals(currentMethodInfo.getSelector(), otherMethodInfo.getSelector()))
		return true;
	if (!CharOperation.equals(currentMethodInfo.getMethodDescriptor(), otherMethodInfo.getMethodDescriptor()))
		return true;
	if (!CharOperation.equals(currentMethodInfo.getGenericSignature(), otherMethodInfo.getGenericSignature()))
		return true;

	char[][] currentThrownExceptions = currentMethodInfo.getExceptionTypeNames();
	char[][] otherThrownExceptions = otherMethodInfo.getExceptionTypeNames();
	if (currentThrownExceptions != otherThrownExceptions) { // TypeConstants.NoExceptions
		int currentThrownExceptionsLength = currentThrownExceptions == null ? 0 : currentThrownExceptions.length;
		int otherThrownExceptionsLength = otherThrownExceptions == null ? 0 : otherThrownExceptions.length;
		if (currentThrownExceptionsLength != otherThrownExceptionsLength)
			return true;
		for (int k = 0; k < currentThrownExceptionsLength; k++)
			if (!CharOperation.equals(currentThrownExceptions[k], otherThrownExceptions[k]))
				return true;
	}
	return false;
}

/**
 * This method is used to fully initialize the contents of the receiver. All methodinfos, fields infos
 * will be therefore fully initialized and we can get rid of the bytes.
 */
private void initialize() throws ClassFormatException {
	try {
		for (int i = 0, max = this.fieldsCount; i < max; i++) {
			this.fields[i].initialize();
		}
		for (int i = 0, max = this.methodsCount; i < max; i++) {
			this.methods[i].initialize();
		}
		if (this.innerInfos != null) {
			for (int i = 0, max = this.innerInfos.length; i < max; i++) {
				this.innerInfos[i].initialize();
			}
		}
		if (this.annotations != null) {
			for (int i = 0, max = this.annotations.length; i < max; i++) {
				this.annotations[i].initialize();
			}
		}
		this.getEnclosingMethod();
		reset();
	} catch(RuntimeException e) {
		ClassFormatException exception = new ClassFormatException(e, this.classFileName);
		throw exception;
	}
}

/**
 * Answer true if the receiver is an anonymous type, false otherwise
 *
 * @return <CODE>boolean</CODE>
 */
public boolean isAnonymous() {
	if (this.innerInfo == null) return false;
	char[] innerSourceName = this.innerInfo.getSourceName();
	return (innerSourceName == null || innerSourceName.length == 0);
}

/**
 * Answer whether the receiver contains the resolved binary form
 * or the unresolved source form of the type.
 * @return boolean
 */
public boolean isBinaryType() {
	return true;
}

/**
 * Answer true if the receiver is a local type, false otherwise
 *
 * @return <CODE>boolean</CODE>
 */
public boolean isLocal() {
	if (this.innerInfo == null) return false;
	if (this.innerInfo.getEnclosingTypeName() != null) return false;
	char[] innerSourceName = this.innerInfo.getSourceName();
	return (innerSourceName != null && innerSourceName.length > 0);
}

/**
 * Answer true if the receiver is a member type, false otherwise
 *
 * @return <CODE>boolean</CODE>
 */
public boolean isMember() {
	if (this.innerInfo == null) return false;
	if (this.innerInfo.getEnclosingTypeName() == null) return false;
	char[] innerSourceName = this.innerInfo.getSourceName();
	return (innerSourceName != null && innerSourceName.length > 0);	 // protection against ill-formed attributes (67600)
}

/**
 * Answer true if the receiver is a nested type, false otherwise
 *
 * @return <CODE>boolean</CODE>
 */
public boolean isNestedType() {
	return this.innerInfo != null;
}

/**
 * Answer the source file name attribute. Return null if there is no source file attribute for the receiver.
 *
 * @return char[]
 */
public char[] sourceFileName() {
	return this.sourceFileName;
}

public String toString() {
	java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
	java.io.PrintWriter print = new java.io.PrintWriter(out);
	print.println(getClass().getName() + "{"); //$NON-NLS-1$
	print.println(" this.className: " + new String(getName())); //$NON-NLS-1$
	print.println(" this.superclassName: " + (getSuperclassName() == null ? "null" : new String(getSuperclassName()))); //$NON-NLS-2$ //$NON-NLS-1$
	print.println(" access_flags: " + printTypeModifiers(accessFlags()) + "(" + accessFlags() + ")"); //$NON-NLS-1$ //$NON-NLS-3$ //$NON-NLS-2$
	print.flush();
	return out.toString();
}
}
