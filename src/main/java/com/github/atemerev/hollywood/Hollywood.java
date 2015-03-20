package com.github.atemerev.hollywood;

import com.github.atemerev.hollywood.annotations.Initial;
import com.github.atemerev.hollywood.annotations.State;
import com.github.atemerev.pms.listeners.dispatch.DispatchListener;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Alexander Kuklev, Alexander Temerev
 * @version $Id$
 */
public class Hollywood {

    // I put on my robe and wizard hat...

    public static final Executor hollywoodExecutor = new ThreadPoolExecutor(4, 64, 300, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    private static final HollywoodClassLoader cl = new HollywoodClassLoader(Hollywood.class);

    private static final Map<Class, Class> actorProxyClassCache = new ConcurrentHashMap<Class, Class>();
    private static final Map<Class, Class> stateProxyClassCache = new ConcurrentHashMap<Class, Class>();
    private static final Map<Class, RootState> sterileStateCache = new ConcurrentHashMap<Class, RootState>();
    private static final Map<Class, ForwardingDispatchListener> stateDispatchListenerCache = new ConcurrentHashMap<Class, ForwardingDispatchListener>();

    //创建Actor
    @SuppressWarnings({"unchecked"})
    public static <T extends Actor> T createActor(Class<T> aClass) {
        try {
            //生成Actor的代理类
            Class actorProxyClass = generateActorProxy(aClass);
            //创建出Actor实例对象
            T actorInstance = (T) actorProxyClass.newInstance();

            Class initialState = null;

            // Ensure everything is OK with states and find the initial state
            // 确保类的各种状态都定义正确,并找出初始状态
            for (Class memberClass : aClass.getDeclaredClasses()) {
                if (memberClass.isAnnotationPresent(State.class)) {
                    int mod = memberClass.getModifiers();
                    if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) || !Modifier.isAbstract(mod)) {
                        throw new IllegalArgumentException("State classes need to have 'public static abstract' signature");
                    }
                    //aClass是一个Actor:因为调用createActor,参数一定是Actor类型. 要求表示状态的内部类必须继承当前Actor
                    if (!aClass.isAssignableFrom(memberClass)) {
                        throw new IllegalArgumentException("State classes must inherit from the actor class");
                    }
                    //如果某个类有@Initial注解,则表示创建完Actor后,Actor进入的初始状态
                    if (memberClass.isAnnotationPresent(Initial.class)) {
                        if (initialState != null) {
                            throw new IllegalArgumentException("Only one state should be marked as @Initial");
                        }
                        initialState = memberClass;

                        try {
                            Method rootOnEnter = RootState.class.getMethod("onEnter");
                            for (Class i = memberClass; aClass.isAssignableFrom(i); i = i.getSuperclass()) {
                                if (!i.getMethod("onEnter").equals(rootOnEnter)) {
                                    throw new IllegalArgumentException("Initial states are not allowed to have direct or inherited onEnter()");
                                }
                            }
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();  // No chance
                        }
                    }
                }
            }
            if (initialState == null) {
                throw new IllegalArgumentException("Some state should be marked as @Initial");
            }
            //设置Actor实例的初始状态(即创建完Actor的状态)
            //actorInstance是传入的参数Class<T> aClass中的T实例.比如Secretary实例.
            //而Secretary中并没有setState方法.它是如何生成这个方法的? 就是通过最开始的生成代理类.
            actorInstance.setState(initialState);
            return actorInstance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    private static Class generateActorProxy(Class aClass) {
        //Actor类-->其代理类
        Class cachedClass = actorProxyClassCache.get(aClass);
        if (cachedClass != null) return cachedClass;

        try {
            // Prepare proxy class node
            ClassWriter cw = new ClassWriter(0);
            ClassNode proxyClassNode = new ClassNode();
            String proxyClassName = AsmUtil.asmClassName(aClass) + "_hwproxy";
            AsmUtil.fillClassNode(proxyClassNode, proxyClassName, AsmUtil.asmClassName(aClass));

            // Create a field containing the current state 添加2个字段
            proxyClassNode.fields.add(new FieldNode(ACC_PRIVATE, "$state",
                    "Lcom/github/atemerev/hollywood/RootState;", null, null));
            proxyClassNode.fields.add(new FieldNode(ACC_PRIVATE, "$prevState",
                    "Lcom/github/atemerev/hollywood/RootState;", null, null));

            // Read actor class node
            ClassNode actorClassNode = new ClassNode();
            ClassReader cr = new ClassReader(aClass.getName());
            cr.accept(actorClassNode, 0);

            // Check that there are no public fields, otherwise throw exception
            for (FieldNode field : (List<FieldNode>) actorClassNode.fields) {
                if ((field.access & ACC_PUBLIC) != 0) {
                    throw new IllegalArgumentException("Public fields are not allowed in the class: " + aClass.getName());
                }
            }

            // Forward all method calls to current state
            List<MethodNode> methods = actorClassNode.methods;
            for (MethodNode mn : methods) {
                if (!mn.name.equals("<init>") && (mn.access & ACC_STATIC) == 0) {
                    createProxyMethod(aClass, proxyClassNode, proxyClassName, mn);
                }
            }

            // Read actor proxy prototype class
            ClassNode actorProxyPrototypeNode = new ClassNode();
            ClassReader cr2 = new ClassReader(ActorProxyPrototype.class.getName());
            cr2.accept(actorProxyPrototypeNode, 0);


            // Hotswap the members form the proxy prototype
            for (MethodNode mn : (List<MethodNode>) actorProxyPrototypeNode.methods) {
                // The <init> method shouldn't be overrided
                if (!mn.name.equals("<init>")) {
                    // Set the right type for "this" argument
                    LocalVariableNode zeroArg = (LocalVariableNode) mn.localVariables.get(0);
                    zeroArg.desc = "L" + proxyClassName + ";";

                    // Set correct owner type for accessed field
                    for (Iterator i = mn.instructions.iterator(); i.hasNext();) {
                        AbstractInsnNode in = (AbstractInsnNode) i.next();
                        if (in instanceof FieldInsnNode) {
                            FieldInsnNode fieldInsnNode = (FieldInsnNode) in;
                            if (fieldInsnNode.owner.equals("com/github/atemerev/hollywood/Hollywood$ActorProxyPrototype")) {
                                fieldInsnNode.owner = proxyClassName;
                            }
                        }
                    }
                    proxyClassNode.methods.add(mn);
                }
            }

            // Create the constructor
            AsmUtil.createDefaultConstructor(aClass, proxyClassNode);

            // Create a class from the class node
            proxyClassNode.accept(cw);
            Class result = cl.defineClass(aClass.getName() + "_hwproxy", cw.toByteArray());
            actorProxyClassCache.put(aClass, result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings({"unchecked"})
    private static void createProxyMethod(Class aClass, ClassNode proxyClassNode, String proxyClassName, MethodNode mn) {
        // Clear the abstract flag
        mn.access &= ~ACC_ABSTRACT;

        // Check for the disallowed package visibility
        if (mn.access == 0) {
            throw new IllegalArgumentException("Package visibility not supported for method: " + aClass.getSimpleName() + "#" + mn.name);
        }

        // Check for the disallowed final modifier
        if ((mn.access & ACC_FINAL) != 0) {
            throw new IllegalArgumentException("Actions cannot be final (method: " + aClass.getSimpleName() + "#" + mn.name + ")");
        }

        // Preliminary fill method node
        mn.maxStack = 5;
        mn.maxLocals = 10;
        mn.instructions = new InsnList();
        mn.tryCatchBlocks = new ArrayList();
        mn.localVariables = new ArrayList();                            

        // Insert the forwarding code:
        InsnList il = mn.instructions;

        // Load "this"
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new FieldInsnNode(GETFIELD, proxyClassName, "$state", "Lcom/github/atemerev/hollywood/RootState;"));
        il.add(new TypeInsnNode(CHECKCAST, AsmUtil.asmClassName(aClass)));


        // Load other arguments
        int sp = 1;
        for (Type argument : Type.getArgumentTypes(mn.desc)) {
            String desc = argument.getDescriptor();
            int loadInstruction = desc.matches("[ZCBSI]") ? ILOAD
                    : desc.equals("J") ? LLOAD
                    : desc.equals("F") ? FLOAD
                    : desc.equals("D") ? DLOAD
                    : ALOAD;
            il.add(new VarInsnNode(loadInstruction, sp));

            int dim = desc.matches("[DF]") ? 2 : 1;
            sp += dim;
            mn.maxStack += dim;
            mn.maxLocals += dim;
        }

        // Call the corresponding method form the state
        il.add(new MethodInsnNode(INVOKEVIRTUAL, AsmUtil.asmClassName(aClass), mn.name, mn.desc));
        String desc = Type.getReturnType(mn.desc).getDescriptor();

        // Return
        int returnInstruction = desc.equals("V") ? RETURN
                : desc.matches("[ZCBSI]") ? IRETURN
                : desc.equals("J") ? LRETURN
                : desc.equals("F") ? FRETURN
                : desc.equals("D") ? DRETURN
                : ARETURN;
        il.add(new InsnNode(returnInstruction));

        // Save the method
        proxyClassNode.methods.add(mn);
    }

    // sterile = true generates a sterile state instance for instanceof applications
    @SuppressWarnings({"unchecked"})
    public static <T extends RootState> T loadStateInstance(Class<T> aClass, boolean sterile) {
        // Check if it's a state at all
        State stateAnnotation = aClass.getAnnotation(State.class);
        if (stateAnnotation == null) {
            throw new IllegalArgumentException("Not a state: " + aClass.getName());
        }

        try {
            if (sterile) {
                RootState cached = sterileStateCache.get(aClass);
                if (cached != null) return (T) cached;
            }
            Class actorProxyClass = generateStateProxy(aClass, sterile);
            T result = (T) actorProxyClass.newInstance();
            if (sterile) {
                sterileStateCache.put(aClass, result);
            } else if (stateDispatchListenerCache.get(aClass) == null) {
                stateDispatchListenerCache.put(aClass, new ForwardingDispatchListener(result));
            }
            return result;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    private static Class generateStateProxy(Class aClass, boolean sterile) {

        if (!sterile) {
            Class cached = stateProxyClassCache.get(aClass);
            if (cached != null) return cached;
        }

        try {
            // Prepare proxy class node
            ClassWriter cw = new ClassWriter(0);
            ClassNode proxyClassNode = new ClassNode();
            AsmUtil.fillClassNode(proxyClassNode, AsmUtil.asmClassName(aClass) + (sterile ? "_hwcproxy" : "_hwproxy"),
                    AsmUtil.asmClassName(aClass));

            // Read state class node
            ClassNode stateClassNode = new ClassNode();
            ClassReader cr = new ClassReader(aClass.getName());
            cr.accept(stateClassNode, 0);
            Map<String, MethodNode> methods = new HashMap<String, MethodNode>();
            for (MethodNode mn : (List<MethodNode>) stateClassNode.methods) {
                methods.put(mn.name + ":" + mn.desc, mn);
                if (!sterile) {
                    if (mn.name.equals("onEnter")) {
                        mn.name += "$" + aClass.getSimpleName();
                        proxyClassNode.methods.add(mn);
                    } else if (mn.name.equals("onExit")) {
                        mn.name += "$" + aClass.getSimpleName();
                        proxyClassNode.methods.add(mn);
                    }
                }
            }

            // Aggregate abstract methods and copy onExits/onEnters
            Class i = aClass;
            while (!i.getSuperclass().equals(Actor.class)) {
                i = i.getSuperclass();
                ClassNode j = new ClassNode();
                cr = new ClassReader(i.getName());
                cr.accept(j, 0);
                for (MethodNode mn : (List<MethodNode>) j.methods) {
                    if (!methods.containsKey(mn.name + ":" + mn.desc)) {
                        methods.put(mn.name + ":" + mn.desc, mn);
                    }
                    if (!sterile) {
                        if (mn.name.equals("onEnter")) {
                            mn.name += "$" + i.getSimpleName();
                            proxyClassNode.methods.add(mn);
                        } else if (mn.name.equals("onExit")) {
                            mn.name += "$" + i.getSimpleName();
                            proxyClassNode.methods.add(mn);
                        }
                    }
                }
            }

            // Check the state has no public fields
            for (FieldNode field : (List<FieldNode>) stateClassNode.fields) {
                if ((field.access & ACC_PUBLIC) != 0) {
                    throw new IllegalArgumentException("Public fields are not allowed in the class: " + aClass.getName());
                }
            }

            if (sterile) {
                sterilizeMethods(proxyClassNode, methods.values());
            } else {
                forwardActorMethods(proxyClassNode, AsmUtil.asmClassName(i) + "_hwproxy");
                fillAbstractMethods(proxyClassNode, aClass, methods.values());
            }

            AsmUtil.createDefaultConstructor(aClass, proxyClassNode);

            // Create a class from the class node
            proxyClassNode.accept(cw);
            Class result = cl.defineClass(aClass.getName() + (sterile ? "_hwcproxy" : "_hwproxy"), cw.toByteArray());
            if (!sterile) {
                stateProxyClassCache.put(aClass, result);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    private static void fillAbstractMethods(ClassNode resultClassNode, Class sourceClass, Collection<MethodNode> methods) {
        // Produce stubs for all abstract methods of the state and put them into the proxy
        for (MethodNode mn : methods) {
            if ((mn.access & ACC_ABSTRACT) != 0 && (mn.access & ACC_STATIC) == 0) {
                // Clear abstract flag
                mn.access &= ~ACC_ABSTRACT;

                // Check for the unsupported package visibility
                if (mn.access == 0) {
                    throw new IllegalArgumentException("Package visibility not supported for method: " + sourceClass.getSimpleName() + "#" + mn.name);
                }

                // Fill the method with exception throwing code
                mn.tryCatchBlocks = new ArrayList();
                mn.localVariables = new ArrayList();
                InsnList il = mn.instructions;
                il.add(new TypeInsnNode(NEW, "java/lang/UnsupportedOperationException"));
                il.add(new InsnNode(DUP));
                il.add(new LdcInsnNode("Method \"" + mn.name + "\" not supported in the state [" + sourceClass.getSimpleName() + "]"));
                il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V"));
                il.add(new InsnNode(ATHROW));
                mn.maxStack = 3;
                mn.maxLocals = Type.getArgumentTypes(mn.desc).length + 10;//+ 2;

                // Save it
                resultClassNode.methods.add(mn);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private static void sterilizeMethods(ClassNode resultClassNode, Collection<MethodNode> sourceClassMethods) {
        for (MethodNode mn : sourceClassMethods) {
            if (!mn.name.equals("<init>") && !mn.name.equals("toString") && (mn.access != 0) && (mn.access & ACC_STATIC) == 0) {
                // Clear abstract flag
                mn.access &= ~ACC_ABSTRACT;

                // Fill the method with exception throwing code
                mn.tryCatchBlocks = new ArrayList();
                mn.localVariables = new ArrayList();
                mn.instructions = new InsnList();
                InsnList il = mn.instructions;
                il.add(new TypeInsnNode(NEW, "java/lang/UnsupportedOperationException"));
                il.add(new InsnNode(DUP));
                il.add(new LdcInsnNode("Method invokation not supported. State instances produced by the actor.state() method can be only used for instanceof checking."));
                il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V"));
                il.add(new InsnNode(ATHROW));
                mn.maxStack = 3;
                mn.maxLocals = Type.getArgumentTypes(mn.desc).length + 10; //+ 2;

                // Save it
                resultClassNode.methods.add(mn);
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private static void forwardActorMethods(ClassNode classNode, String actorProxyClassName) {
        try {
            // Create a field containing the actor proxy
            classNode.fields.add(new FieldNode(ACC_PUBLIC, "$actor", "Lcom/github/atemerev/hollywood/Actor;", null, null));

            // Read actor proxy prototype class
            ClassNode actorProxyPrototypeNode = new ClassNode();
            ClassReader cr2 = new ClassReader(ActorProxyPrototype.class.getName());
            cr2.accept(actorProxyPrototypeNode, 0);

            // Forward methods defined in actor proxy prototype to the actor
            for (MethodNode mn : (List<MethodNode>) actorProxyPrototypeNode.methods) {
                // The <init> method shouldn't be overrided
                if (!mn.name.equals("<init>")) {

                    // Set the right type for "this" argument
                    LocalVariableNode zeroArg = null;
                    for (Object node : mn.localVariables) {
                        LocalVariableNode localVariableNode = (LocalVariableNode) node;
                        if (localVariableNode.index == 0) {
                            zeroArg = localVariableNode;
                            break;
                        }
                    }
                    zeroArg.desc = "L" + classNode.name + ";";
                    // Preliminary fill method node
                    mn.maxStack = 5;
                    mn.maxLocals = 10;
                    mn.instructions = new InsnList();
                    mn.tryCatchBlocks = new ArrayList();
                    mn.localVariables = new ArrayList();
                    mn.localVariables.add(zeroArg);

                    InsnList il = mn.instructions;

                    // Load "this"
                    il.add(new VarInsnNode(ALOAD, 0));
                    il.add(new FieldInsnNode(GETFIELD, classNode.name, "$actor", "Lcom/github/atemerev/hollywood/Actor;"));
                    il.add(new TypeInsnNode(CHECKCAST, actorProxyClassName));

                    // Load other arguments
                    int sp = 1;
                    for (Type argument : Type.getArgumentTypes(mn.desc)) {
                        String desc = argument.getDescriptor();
                        int loadInstruction = desc.matches("[ZCBSI]") ? ILOAD
                                : desc.equals("J") ? LLOAD
                                : desc.equals("F") ? FLOAD
                                : desc.equals("D") ? DLOAD
                                : ALOAD;
                        il.add(new VarInsnNode(loadInstruction, sp));

                        int dim = desc.matches("[DF]") ? 2 : 1;
                        sp += dim;
                        mn.maxStack += dim;
                        mn.maxLocals += dim;
                    }

                    // Call the corresponding method form the actor

                    il.add(new MethodInsnNode(INVOKEVIRTUAL, actorProxyClassName, mn.name, mn.desc));
                    String desc = Type.getReturnType(mn.desc).getDescriptor();

                    // Return
                    int returnInstruction = desc.equals("V") ? RETURN
                            : desc.matches("[ZCBSI]") ? IRETURN
                            : desc.equals("J") ? LRETURN
                            : desc.equals("F") ? FRETURN
                            : desc.equals("D") ? DRETURN
                            : ARETURN;
                    il.add(new InsnNode(returnInstruction));

                    // Save the method
                    classNode.methods.add(mn);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // Cannot occur
        }
    }

    public static ForwardingDispatchListener getDispListener(Class aClass) {
        return stateDispatchListenerCache.get(aClass);
    }

    private static class HollywoodClassLoader extends ClassLoader {

        HollywoodClassLoader(Class aClass) {
            super(aClass.getClassLoader());
        }

        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    // Actor代理的原型. 通过ASM会给具体的Actor修改成满足自己逻辑的实现类
    @SuppressWarnings({"unchecked"})
    static class ActorProxyPrototype {
        private RootState $prevState;
        private RootState $state;

        public final Actor me() {
            return (Actor) (Object) this;
        }

        public final RootState state() {
            // Returns sterilized version of the current state.
            return Hollywood.loadStateInstance((Class<RootState>) $state.getClass().getSuperclass(), true);
        }

        public final RootState prevState() {
            // Returns sterilized version of the current state.
            return Hollywood.loadStateInstance((Class<RootState>) $prevState.getClass().getSuperclass(), true);
        }

        protected final <T extends RootState> T prepareState(Class<T> targetStateClass) {
            Class actorClass = this.getClass().getSuperclass();
            T targetState = Hollywood.loadStateInstance(targetStateClass, false);

            // Check if it's a state of our actor
            if (!actorClass.isAssignableFrom(targetStateClass)) {
                throw new IllegalArgumentException("State is not bound to current actor: " + targetStateClass.getName());
            }

            // Save pointer to the actor
            try {
                targetState.getClass().getField("$actor").set(targetState, this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if ($state == null) {
                return targetState;
            }

            Class presentStateClass = $state.getClass().getSuperclass();
            Class nca; // nearest common ancestor

            RootState oldState = $state;
            // Call onExit()s of the state (and all it's ancestors) and determine NCA
            for (nca = presentStateClass; actorClass.isAssignableFrom(nca)
                    && !(nca.isAssignableFrom(targetStateClass)); nca = nca.getSuperclass()) {
                try {
                    Method onExit = $state.getClass().getDeclaredMethod("onExit$" + nca.getSimpleName());
                    onExit.setAccessible(true);
                    onExit.invoke($state);
                    if ($state != oldState) return null; // TODO explain
                } catch (NoSuchMethodException e) {
                    // No Problem
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                    // PANIC! Actor left in inconsistent state!
                }
            }

            // Carry over the fields shared by the old and the new state
            for (Class i = nca; actorClass.isAssignableFrom(i); i = i.getSuperclass()) {
                for (Field field : i.getDeclaredFields()) {
                    try {
                        field.setAccessible(true);
                        field.set(targetState, field.get($state));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return targetState;
        }

        protected final synchronized <T extends RootState> T setState(T targetState) {
            Class targetStateClass = targetState.getClass().getSuperclass();
            Class actorClass = this.getClass().getSuperclass();

            if ($state == null) {
                $state = targetState;
                return targetState;
            }

            Class presentStateClass = $state.getClass().getSuperclass();
            Class nca; // nearest common ancestor

            // Determine NCA
            for (nca = presentStateClass; actorClass.isAssignableFrom(nca)
                    && !(nca.isAssignableFrom(targetStateClass)); nca = nca.getSuperclass());

            // Generate a list of the new state ancestor list up to NCA
            LinkedList<Class> ancestorsToEnter = new LinkedList<Class>();
            for (Class j = targetState.getClass().getSuperclass(); j != nca; j = j.getSuperclass()) {
                ancestorsToEnter.addFirst(j);
            }

            // Call onEnter()s of the state (and all it's ancestors) we're entering
            $prevState = $state;
            $state = targetState;
            Hollywood.getDispListener($state.getClass().getSuperclass())
                    .forwardMessage(new StateChangedEvent(), (Actor) $state);
            for (Class cl : ancestorsToEnter) {
                try {
                    Method onEnter = cl.getDeclaredMethod("onEnter");
                    onEnter.setAccessible(true);
                    onEnter.invoke(targetState);
                    if ($state != targetState) return null;
                } catch (NoSuchMethodException e) {
                    // No Problem
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                    // PANIC! Actor left in inconsistent state!
                }
            }

            return targetState;
        }

        public final synchronized void processMessage(Object message) {
            Hollywood.getDispListener($state.getClass().getSuperclass())
                    .forwardMessage(message, (Actor) $state);
        }
    }

    protected static class ForwardingDispatchListener extends DispatchListener {
        ForwardingDispatchListener(Object target) {
            super(target);
        }

        public void forwardMessage(Object message, Actor target) {
            listener = target;
            setExecutor(target.asyncListenerExecutor());
            processMessage(message);
        }
    }

    private static class AsmUtil {
        @SuppressWarnings({"unchecked"})
        static void createDefaultConstructor(Class aClass, ClassNode proxyClassNode) {
            MethodNode consNode = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);
            consNode.maxStack = 1;
            consNode.maxLocals = 10;
            InsnList consInstructions = new InsnList();
            consInstructions.add(new VarInsnNode(ALOAD, 0));
            consInstructions.add(new MethodInsnNode(INVOKESPECIAL, asmClassName(aClass), "<init>", "()V"));
            consInstructions.add(new InsnNode(RETURN));
            consNode.instructions = consInstructions;
            proxyClassNode.methods.add(consNode);
        }

        static String asmClassName(Class aClass) {
            return Type.getInternalName(aClass);
        }

        static void fillClassNode(ClassNode cn, String name, String superName) {
            cn.version = V1_5;
            cn.access = ACC_PUBLIC;
            cn.name = name;
            cn.superName = superName;
        }
    }
}
