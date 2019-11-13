package net.hasor.dataql.compiler.qil;
import net.hasor.dataql.compiler.ast.Inst;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CompilerContext {
    private CompilerEnvironment compilerEnvironment;
    private Stack<List<String>> dataStack = new Stack<List<String>>() {{
        push(new ArrayList<>());
    }};

    public CompilerContext(CompilerEnvironment compilerEnvironment) {
        this.compilerEnvironment = compilerEnvironment;
    }

    public <T extends Inst> InstCompilerExecutor findInstCompilerByInst(T instObject) {
        Class<T> instClass = (Class<T>) instObject.getClass();
        InstCompiler<T> instCompiler = this.compilerEnvironment.findInstCompilerByType(instClass);
        return queue -> instCompiler.doCompiler(instObject, queue, CompilerContext.this);
    }

    public void newFrame() {
        this.dataStack.push(new ArrayList<>());
    }

    public void dropFrame() {
        this.dataStack.pop();
    }

    /** 当前栈中是否存在该元素，如果存在返回位置 */
    public int containsWithCurrent(String target) {
        if (this.dataStack.isEmpty()) {
            return -1;
        } else {
            return this.dataStack.peek().indexOf(target);
        }
    }

    /** 当前栈中是否存在该元素，如果存在返回位置 */
    public ContainsIndex containsWithTree(String target) {
        ContainsIndex index = new ContainsIndex();
        index.depth = -1; // <-无效值
        index.index = -1; // <-无效值
        //
        int stackSize = this.dataStack.size();
        for (int i = 0; i < this.dataStack.size(); i++) {
            index.current = i == 0;
            index.depth = stackSize - i - 1;
            List<String> stringList = this.dataStack.get(index.depth);
            int indexOf = stringList.indexOf(target);
            if (indexOf >= 0) {
                index.index = indexOf;
                return index;
            }
        }
        //
        index.depth = 0;
        return index;
    }

    /** 将 name 压入栈，并返回在栈中的位置 */
    public int push(String target) {
        List<String> nameStack = this.dataStack.peek();
        nameStack.add(target);
        return nameStack.indexOf(target);
    }

    /** 当前深度 */
    public int getDepth() {
        return this.dataStack.size() - 1;
    }

    public static class ContainsIndex {
        public int     depth   = 0;
        public int     index   = 0;
        public boolean current = false;

        //
        public boolean isValid() {
            return this.depth >= 0 && this.index >= 0;
        }
    }
}
