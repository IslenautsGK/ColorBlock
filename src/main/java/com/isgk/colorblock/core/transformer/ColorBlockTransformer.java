package com.isgk.colorblock.core.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.isgk.colorblock.core.ColorBlockCore;

import net.minecraft.launchwrapper.IClassTransformer;

public class ColorBlockTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (name.equals(ColorBlockCore.debug ? "net.minecraft.client.particle.ParticleManager" : "btg")) {
			ClassReader classReader = new ClassReader(basicClass);
			ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
			ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM4, classWriter) {

				@Override
				public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
					if (name.equals("g")) {
						access = Opcodes.ACC_PUBLIC;
					}
					return cv.visitField(access, name, desc, signature, value);
				}

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature,
						String[] exceptions) {
					if (ColorBlockCore.debug ? name.equals("updateEffects")
							: (name.equals("a") && desc.equals("()V"))) {
						MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
						return new MethodVisitor(Opcodes.ASM4, mv) {

							@Override
							public void visitIntInsn(int opcode, int operand) {
								if (opcode == Opcodes.SIPUSH && operand == 16384) {
									super.visitLdcInsn(ColorBlockCore.maxParticleCount);
								} else {
									super.visitIntInsn(opcode, operand);
								}
							}

						};
					} else if (ColorBlockCore.parallelParticleUpdate
							&& (ColorBlockCore.debug ? name.equals("updateEffectLayer")
									: (name.equals("a") && desc.equals("(I)V")))) {
						MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
						return new MethodVisitor(Opcodes.ASM4, mv) {

							@Override
							public void visitMethodInsn(int opcode, String owner, String name, String desc,
									boolean itf) {
								if (opcode == Opcodes.INVOKESPECIAL
										&& (ColorBlockCore.debug ? name.equals("tickParticleList")
												: (name.equals("a") && desc.equals("(Ljava/util/Queue;)V")))) {
									super.visitMethodInsn(opcode, owner, name, "(Ljava/util/ArrayDeque;)V", itf);
								} else {
									super.visitMethodInsn(opcode, owner, name, desc, itf);
								}
							};

						};
					} else if (ColorBlockCore.parallelParticleUpdate
							&& (ColorBlockCore.debug ? name.equals("tickParticleList")
									: (name.equals("a") && desc.equals("(Ljava/util/Queue;)V")))) {
						MethodVisitor mv = cv.visitMethod(access, name, "(Ljava/util/ArrayDeque;)V", signature,
								exceptions);
						mv.visitCode();
						Label start = new Label();
						mv.visitLabel(start);
						mv.visitVarInsn(Opcodes.ALOAD, 1);
						mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/isgk/colorblock/core/util/CoreUtil",
								"tickParticleList", "(Ljava/util/ArrayDeque;)V", false);
						mv.visitInsn(Opcodes.RETURN);
						Label end = new Label();
						mv.visitLabel(end);
						mv.visitLocalVariable("this", "Lbtg;", null, start, end, 0);
						mv.visitLocalVariable("particles", "Ljava/util/ArrayDeque;", null, start, end, 1);
						mv.visitMaxs(1, 2);
						mv.visitEnd();
						return null;
					}
					if (cv != null) {
						return cv.visitMethod(access, name, desc, signature, exceptions);
					}
					return null;
				}

			};
			classReader.accept(classVisitor, Opcodes.ASM4);
			return classWriter.toByteArray();
		} else if (name.equals(ColorBlockCore.debug ? "net.minecraft.client.particle.Particle" : "btf")) {
			ClassReader classReader = new ClassReader(basicClass);
			ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
			ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM4, classWriter) {

				@Override
				public void visit(int version, int access, String name, String signature, String superName,
						String[] interfaces) {
					super.visit(version, access, name, signature, superName, interfaces);
					if (cv != null) {
						if (!ColorBlockCore.debug) {
							FieldVisitor fv = cv.visitField(Opcodes.ACC_PUBLIC, "brightness", "I", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "exe",
									"Lcom/isgk/colorblock/util/commoninterface/IExecutable;", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "moveT", "D", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "step", "D", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "index", "I", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "centerX", "D", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "centerY", "D", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "centerZ", "D", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "pictures", "[I", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "pictureIndex", "I", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "autoDestory", "Z", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "matrix", "Lcom/isgk/colorblock/util/matrix/Matrix;",
									null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "customMove", "Z", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "stop", "Z", null, null);
							fv.visitEnd();
							fv = cv.visitField(Opcodes.ACC_PUBLIC, "pAge", "I", null, null);
							fv.visitEnd();
						}
						MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,
								ColorBlockCore.debug ? "getBrightnessForRender" : "a", "(F)I", null, null);
						mv.visitCode();
						Label start = new Label();
						mv.visitLabel(start);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitFieldInsn(Opcodes.GETFIELD, name, "brightness", "I");
						mv.visitInsn(Opcodes.ICONST_M1);
						Label eq = new Label();
						mv.visitJumpInsn(Opcodes.IF_ICMPEQ, eq);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitFieldInsn(Opcodes.GETFIELD, name, "brightness", "I");
						mv.visitInsn(Opcodes.IRETURN);
						mv.visitLabel(eq);
						mv.visitFrame(Opcodes.F_SAME, 0, new Object[] { null, null }, 0, new Object[] { null, null });
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitVarInsn(Opcodes.FLOAD, 1);
						mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name,
								ColorBlockCore.debug ? "getBrightnessForRender2" : "a2", "(F)I", false);
						mv.visitInsn(Opcodes.IRETURN);
						Label end = new Label();
						mv.visitLabel(end);
						mv.visitLocalVariable("this", "L" + name + ";", null, start, end, 0);
						mv.visitLocalVariable("partialTicks", "F", null, start, end, 1);
						mv.visitMaxs(2, 2);
						mv.visitEnd();
						mv = cv.visitMethod(Opcodes.ACC_PUBLIC, ColorBlockCore.debug ? "move" : "a", "(DDD)V", null,
								null);
						mv.visitCode();
						start = new Label();
						mv.visitLabel(start);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitFieldInsn(Opcodes.GETFIELD, name, "customMove", "Z");
						eq = new Label();
						mv.visitJumpInsn(Opcodes.IFEQ, eq);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/isgk/colorblock/core/util/CoreUtil", "move",
								"(L" + name + ";)V", false);
						Label returnLabel = new Label();
						mv.visitJumpInsn(Opcodes.GOTO, returnLabel);
						mv.visitLabel(eq);
						mv.visitFrame(Opcodes.F_SAME, 0, new Object[] { null, null, null, null, null, null, null }, 0,
								new Object[] { null, null, null, null, null, null, null });
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitFieldInsn(Opcodes.GETFIELD, name, "stop", "Z");
						mv.visitJumpInsn(Opcodes.IFNE, returnLabel);
						mv.visitVarInsn(Opcodes.ALOAD, 0);
						mv.visitVarInsn(Opcodes.DLOAD, 1);
						mv.visitVarInsn(Opcodes.DLOAD, 3);
						mv.visitVarInsn(Opcodes.DLOAD, 5);
						mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name, ColorBlockCore.debug ? "move2" : "a2", "(DDD)V",
								false);
						mv.visitLabel(returnLabel);
						mv.visitFrame(Opcodes.F_SAME, 0, new Object[] { null, null, null, null, null, null, null }, 0,
								new Object[] { null, null, null, null, null, null, null });
						mv.visitInsn(Opcodes.RETURN);
						end = new Label();
						mv.visitLabel(end);
						mv.visitLocalVariable("this", "L" + name + ";", null, start, end, 0);
						mv.visitLocalVariable("x", "D", null, start, end, 1);
						mv.visitLocalVariable("y", "D", null, start, end, 3);
						mv.visitLocalVariable("z", "D", null, start, end, 5);
						mv.visitMaxs(7, 7);
						mv.visitEnd();
					}
				}

				@Override
				public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
					if (name.equals("g") || name.equals("h") || name.equals("i") || name.equals("j") || name.equals("k")
							|| name.equals("l") || name.equals("x") || name.equals("D")) {
						access = Opcodes.ACC_PUBLIC;
					}
					return cv.visitField(access, name, desc, signature, value);
				}

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature,
						String[] exceptions) {
					if (name.equals("<init>")) {
						return new MethodVisitor(Opcodes.ASM4,
								cv.visitMethod(access, name, desc, signature, exceptions)) {

							public void visitInsn(int opcode) {
								if (opcode == Opcodes.RETURN) {
									mv.visitVarInsn(Opcodes.ALOAD, 0);
									mv.visitInsn(Opcodes.ICONST_M1);
									mv.visitFieldInsn(Opcodes.PUTFIELD,
											ColorBlockCore.debug ? "net/minecraft/client/particle/Particle" : "btf",
											"brightness", "I");
									mv.visitVarInsn(Opcodes.ALOAD, 0);
									mv.visitInsn(Opcodes.ICONST_0);
									mv.visitFieldInsn(Opcodes.PUTFIELD,
											ColorBlockCore.debug ? "net/minecraft/client/particle/Particle" : "btf",
											"pAge", "I");
								}
								mv.visitInsn(opcode);
							};

						};
					} else if (ColorBlockCore.debug ? (name.equals("getBrightnessForRender") || name.equals("move"))
							: (name.equals("a") && (desc.equals("(F)I") || desc.equals("(DDD)V")))) {
						return cv.visitMethod(access, name + "2", desc, signature, exceptions);
					} else if (ColorBlockCore.debug && name.equals("move2")) {
						return null;
					}
					return cv.visitMethod(access, name, desc, signature, exceptions);
				}

			};
			classReader.accept(classVisitor, Opcodes.ASM4);
			return classWriter.toByteArray();
		} else if (transformedName.startsWith("net.minecraft.client.particle.")) {
			ClassReader classReader = new ClassReader(basicClass);
			ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
			ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM4, classWriter) {

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature,
						String[] exceptions) {
					if (ColorBlockCore.debug ? (name.equals("getBrightnessForRender") || name.equals("move"))
							: (name.equals("a") && (desc.equals("(F)I") || desc.equals("(DDD)V")))) {
						return new MethodVisitor(Opcodes.ASM4,
								cv.visitMethod(access, name + "2", desc, signature, exceptions)) {

							public void visitMethodInsn(int opcode, String owner, String name, String desc,
									boolean itf) {
								if (opcode == Opcodes.INVOKESPECIAL && ColorBlockCore.debug
										? (name.equals("getBrightnessForRender") || name.equals("move"))
										: (name.equals("a") && (desc.equals("(F)I") || desc.equals("(DDD)V")))) {
									name += "2";
								}
								super.visitMethodInsn(opcode, owner, name, desc, itf);
							};

						};
					} else if (ColorBlockCore.debug ? name.equals("onUpdate")
							: name.equals("a") && desc.equals("()V")) {
						return new MethodVisitor(Opcodes.ASM4,
								cv.visitMethod(access, name, desc, signature, exceptions)) {

							public void visitCode() {
								mv.visitVarInsn(Opcodes.ALOAD, 0);
								mv.visitInsn(Opcodes.DUP);
								mv.visitFieldInsn(Opcodes.GETFIELD,
										ColorBlockCore.debug ? "net/minecraft/client/particle/Particle" : "btf", "pAge",
										"I");
								mv.visitInsn(Opcodes.ICONST_1);
								mv.visitInsn(Opcodes.IADD);
								mv.visitFieldInsn(Opcodes.PUTFIELD,
										ColorBlockCore.debug ? "net/minecraft/client/particle/Particle" : "btf", "pAge",
										"I");
							};

						};
					}
					return cv.visitMethod(access, name, desc, signature, exceptions);
				}

			};
			classReader.accept(classVisitor, Opcodes.ASM4);
			return classWriter.toByteArray();
		}
		return basicClass;
	}

}
