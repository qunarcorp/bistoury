package qunar.tc.bistoury.proxy.util.profiler;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import qunar.tc.bistoury.common.profiler.method.FunctionInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HotSpotMethodParser {

    private static final Pattern countPattern = Pattern.compile("\\d+$");

    public static TreeNode<FunctionCounter> parse(File collapsedFile, Function<List<String>, List<String>> stackFilter) throws IOException {
        return Files.readLines(collapsedFile, Charsets.UTF_8, new StackLineProcessor(stackFilter));
    }

    private static class StackLineProcessor implements LineProcessor<TreeNode<FunctionCounter>> {

        private final FunctionInfo rootInfo = new FunctionInfo("all");

        private final FunctionCounter rootCounter = new FunctionCounter(rootInfo);

        private final TreeNode<FunctionCounter> rootNode = new TreeNode<>(rootCounter);

        private static final Splitter COLON_SPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();

        private final Function<List<String>, List<String>> stackFilter;

        private StackLineProcessor(Function<List<String>, List<String>> stackFilter) {
            this.stackFilter = stackFilter;
        }

        @Override
        public boolean processLine(String line) {
            CallStackCounter stackCounter = createCallStackCounter(line);
            TreeNode<FunctionCounter> curNode = rootNode;
            rootNode.getNode().add(stackCounter.getCount());
            for (FunctionInfo functionInfo : stackCounter.getFunctionInfos()) {
                TreeNode<FunctionCounter> info = curNode.getOrCreate(new FunctionCounter(functionInfo));
                info.getNode().add(stackCounter.getCount());
                curNode = info;
            }
            return true;
        }

        private CallStackCounter createCallStackCounter(String line) {
            Matcher matcher = countPattern.matcher(line);
            matcher.find();
            long count = Long.parseLong(matcher.group());
            List<String> infos = COLON_SPLITTER.splitToList(line.subSequence(0, matcher.start()));
            infos = new ArrayList<>(infos);
            Collections.reverse(infos);
            List<String> compactInfos = stackFilter.apply(infos);
            List<FunctionInfo> stack = Lists.newArrayListWithExpectedSize(compactInfos.size());
            for (String info : compactInfos) {
                FunctionInfo func = new FunctionInfo(info.replace("//", "."));
                stack.add(func);
            }
            return new CallStackCounter(stack, count);
        }

        @Override
        public TreeNode<FunctionCounter> getResult() {
            return rootNode;
        }
    }

}
