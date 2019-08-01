/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qunar.tc.bistoury.instrument.client.location;

import com.taobao.middleware.logger.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import qunar.tc.bistoury.attach.common.BistouryLoggger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Whitelist/Blacklist of methods that are safe to be used in breakpoint expressions. If a method is
 * not listed by this class, then a bytecode analysis should be performed to determine if the method
 * is safe.
 * <p>
 * <p>The data structures in this class never change once initialized, hence the class is thread
 * safe.
 */
public final class MethodsFilter {
    private static final Logger LOG = BistouryLoggger.getLogger();

    /**
     * Policy for class methods (either instance or static).
     */
    public static final class ClassFilter {

        private final boolean allowByDefault;

        private final Set<String> allowedInstanceMethods;

        private final Set<String> allowedStaticMethods;

        private final Set<String> blockedInstanceMethods;

        private final Set<String> blockedStaticMethods;

        private ClassFilter(final Builder builder) {
            allowByDefault = builder.allowByDefault;
            allowedInstanceMethods = Collections.unmodifiableSet(builder.allowedInstanceMethods);
            allowedStaticMethods = Collections.unmodifiableSet(builder.allowedStaticMethods);
            blockedInstanceMethods = Collections.unmodifiableSet(builder.blockedInstanceMethods);
            blockedStaticMethods = Collections.unmodifiableSet(builder.blockedStaticMethods);
        }

        /**
         * @return true if all instance and static methods in the class can be called unless listed in
         * {@link ClassFilter#blockedInstanceMethods} or
         * {@link ClassFilter#blockedStaticMethods}.
         */
        public boolean isAllowByDefault() {
            return allowByDefault;
        }

        /**
         * @return set of instance methods that can be called.
         */
        public Set<String> getAllowedInstanceMethods() {
            return allowedInstanceMethods;
        }

        /**
         * @return set of static methods that can be called.
         */
        public Set<String> getAllowedStaticMethods() {
            return allowedStaticMethods;
        }

        /**
         * @return set of blocked instance methods. Takes precedence over set of allowed.
         */
        public Set<String> getBlockedInstanceMethods() {
            return blockedInstanceMethods;
        }

        /**
         * @return set of blocked static methods. Takes precedence over set of allowed.
         */
        public Set<String> getBlockedStaticMethods() {
            return blockedStaticMethods;
        }

        /**
         * Builder class to present a clear API for setting up {@link ClassFilter}.
         */
        public static final class Builder {
            private boolean allowByDefault;
            private final Set<String> allowedInstanceMethods = new HashSet<>();
            private final Set<String> allowedStaticMethods = new HashSet<>();
            private final Set<String> blockedInstanceMethods = new HashSet<>();
            private final Set<String> blockedStaticMethods = new HashSet<>();

            public Builder setAllowByDefault(final boolean allowByDefault) {
                this.allowByDefault = allowByDefault;
                return this;
            }

            public Builder addAllowedInstanceMethods(final Collection<String> methods) {
                allowedInstanceMethods.addAll(methods);
                return this;
            }

            public Builder addAllowedInstanceMethods(final String... methods) {
                Collections.addAll(allowedInstanceMethods, methods);
                return this;
            }

            public Builder addAllowedStaticMethods(final Collection<String> methods) {
                allowedStaticMethods.addAll(methods);
                return this;
            }

            public Builder addAllowedStaticMethods(final String... methods) {
                Collections.addAll(allowedStaticMethods, methods);
                return this;
            }

            public Builder addBlockedInstanceMethods(final Collection<String> methods) {
                blockedInstanceMethods.addAll(methods);
                return this;
            }

            public Builder addBlockedInstanceMethods(final String... methods) {
                Collections.addAll(blockedInstanceMethods, methods);
                return this;
            }

            public Builder addBlockedStaticMethods(final Collection<String> methods) {
                blockedStaticMethods.addAll(methods);
                return this;
            }

            public Builder addBlockedStaticMethods(final String... methods) {
                Collections.addAll(blockedStaticMethods, methods);
                return this;
            }

            public ClassFilter build() {
                return new ClassFilter(this);
            }
        }
    }

    /**
     * Parses debugger configuration files.
     * <p>
     * <p>The configuration will be usually derived from a single XML file. If multiple files are
     * provided, the effective configuration will be a merge of both. In case of a conflict, the
     * latest definition wins.
     * <p>
     * <p>Multiple configration files are used in test to whitelist additional test specific classes.
     * <p>
     * <p>If {@link Builder#add} is not called at all, {@link Builder#build} will return an empty
     * configuration.
     */
    public static final class Builder {
        private final MethodsFilter instance = new MethodsFilter();

        public Builder add(InputStream configStream) {
            instance.parseXml(configStream);
            return this;
        }

        public MethodsFilter build() {
            return instance;
        }
    }

    /**
     * Set of classes that define safety policy. Classes not listed in this map cannot be used for
     * breakpoint expressions (unless considered immutable by MethodAnalyzer. The map key is
     * class signature.
     */
    private final Map<String, ClassFilter> classFilters = new HashMap<>();

    private MethodsFilter() {
    }

    /**
     * @param classSignature binary name of the class (e.g. "java/lang/String")
     * @param methodName     the name of the method (e.g. "toString")
     * @param isStatic       whether the method is static or not
     * @return true if the method is whitelisted or the class has {@link ClassFilter#allowByDefault}
     * set to true, false if the method is blacklisted, and null if the method is not allowed,
     * but not explicitly blocked
     */
    public Boolean isSafeMethod(final String classSignature, final String methodName,
                                final boolean isStatic) {
        // Check that the signature and name are valid.
        if ((classSignature == null) || classSignature.isEmpty()) {
            return false;
        }
        if ((methodName == null) || methodName.isEmpty()) {
            return false;
        }

        final ClassFilter classFilter = classFilters.get(classSignature);
        if (classFilter == null) {
            // Not explicitly allowed and not explicitly blocked.
            return null;
        }

        final Collection<String> blockedMethods =
                isStatic ? classFilter.blockedStaticMethods : classFilter.blockedInstanceMethods;
        if (blockedMethods.contains(methodName)) {
            // Explicitly blocked.
            return false;
        }

        if (classFilter.allowByDefault) {
            // Explicitly allowed.
            return true;
        }

        final Collection<String> allowedMethods =
                isStatic ? classFilter.allowedStaticMethods : classFilter.allowedInstanceMethods;

        if (allowedMethods.contains(methodName)) {
            // Explicitly allowed.
            return true;
        }

        // Not explicitly allowed and not explicitly blocked.
        return null;
    }

    /**
     * Parse the xml from input stream into the classFilters.
     *
     * @param configInputStream the input stream serving the configuration xml.
     */
    private void parseXml(final InputStream configInputStream) {
        try {
            // Disable validation and other features we don't need (otherwise the configuration file
            // can take 0.5 seconds to load).
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            factory.setFeature("http://xml.org/sax/features/namespaces", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
                    false);
            factory.setFeature(
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false);

            final DocumentBuilder dBuilder = factory.newDocumentBuilder();
            final Document doc = dBuilder.parse(configInputStream);

            // This is equivalent to xpath "//filter". If more tags are added, this might
            // be too broad and will have to be replaced be something equivalent to
            // "/config/filters/filters".
            final NodeList filters = doc.getElementsByTagName("filter");

            // Iterate through each <filter>
            for (int i = 0; i < filters.getLength(); i++) {
                Node filter = filters.item(i);
                if (filter.getNodeType() != Node.ELEMENT_NODE) {
                    continue;  // Ignore XML comments.
                }

                final NamedNodeMap attributes = filter.getAttributes();
                // The class name is an attribute.
                final Node classNode = attributes.getNamedItem("class");
                if (classNode == null) {
                    // If the class attribute is missing, skip this element.
                    LOG.warn("\"class\" element not found");
                    continue;
                }

                final String className = classNode.getNodeValue();
                final NodeList children = filter.getChildNodes();

                // Defaults.
                boolean allowByDefault = false;
                Collection<String> allowedInstanceMethods = Collections.emptySet();
                Collection<String> allowedStaticMethods = Collections.emptySet();
                Collection<String> blockedInstanceMethods = Collections.emptySet();
                Collection<String> blockedStaticMethods = Collections.emptySet();

                for (int j = 0; j < children.getLength(); j++) {
                    final Node child = children.item(j);
                    if (child.getNodeType() != Node.ELEMENT_NODE) {
                        continue;  // Ignore XML comments.
                    }

                    final String nodeName = child.getNodeName();
                    if (nodeName == null) {
                        continue;
                    }

                    switch (nodeName) {
                        case "allowByDefault":
                            allowByDefault = parseBoolean(child, false);
                            break;

                        case "allowedInstanceMethods":
                            allowedInstanceMethods = parseMethodCollection(child,
                                    "allowedInstanceMethod");
                            break;

                        case "allowedStaticMethods":
                            allowedStaticMethods = parseMethodCollection(child,
                                    "allowedStaticMethod");
                            break;

                        case "blockedInstanceMethods":
                            blockedInstanceMethods = parseMethodCollection(child,
                                    "blockedInstanceMethod");
                            break;

                        case "blockedStaticMethods":
                            blockedStaticMethods = parseMethodCollection(child,
                                    "blockedStaticMethod");
                            break;

                        default:
                            LOG.warn("Invalid node name {}", nodeName);
                            break;
                    }
                }

                if (classFilters.containsKey(className)) {
                    LOG.warn("Class {} appears more than once in the configuration",
                            className);
                }

                classFilters.put(className, new ClassFilter.Builder()
                        .setAllowByDefault(allowByDefault)
                        .addAllowedInstanceMethods(allowedInstanceMethods)
                        .addAllowedStaticMethods(allowedStaticMethods)
                        .addBlockedInstanceMethods(blockedInstanceMethods)
                        .addBlockedStaticMethods(blockedStaticMethods)
                        .build());
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LOG.warn("Exception occurred parsing configuration XML file", e);
        }
    }

    /**
     * Read a boolean value from the given xml node.
     *
     * @param node         the xml node that contains a string representation of a boolean.
     * @param defaultValue the default value.
     * @return the boolean value represented by the text content of the node, or the default value if
     * the string is invalid.
     */
    private static boolean parseBoolean(final Node node, final boolean defaultValue) {
        final String text = node.getTextContent();

        // Boolean.parseBoolean() returns false if the String is not a valid boolean String
        // representation.
        if (!text.equals("true") && !text.equals("false")) {
            LOG.warn("Bad boolean value {}", text);
            return defaultValue;
        }

        return Boolean.parseBoolean(text);
    }

    /**
     * Parses a collection of strings.
     *
     * @param node        parent XML element.
     * @param elementName expected names of child elements.
     * @return strings in XML list.
     */
    private static Collection<String> parseMethodCollection(Node node,
                                                            String elementName) {
        final NodeList children = node.getChildNodes();
        final int length = children.getLength();
        final List<String> methods = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            final Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;  // Ignore XML comments.
            }

            if (!child.getNodeName().equals(elementName)) {
                LOG.warn("Unexpected element {} in a string collection ({} expected)",
                        child.getNodeName(), elementName);
                continue;
            }

            final String method = child.getTextContent();
            if (method.isEmpty()) {
                LOG.warn("Empty string element in a collection");
                continue;
            }
            methods.add(method);
        }

        return methods;
    }
}
