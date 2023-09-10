package crawler.Constant;

public class Observer {
//    public static final String DELAY_SCRIPT = Reader.readStringFromFile("src/main/resources/delayScriptExecution.js");
//
//    public static final String ELEMENT_OBSERVER = Reader.readStringFromFile("src/main/resources/ElementStorage.js");

    public static final String DELAY_SCRIPT = "window.scriptsToExecute = [];\n" +
            "window.scriptsToDeferExecute = [];\n" +
            "window.newScriptsToExecute = [];\n" +
            "\n" +
            "window.currentDelayExecuteOldScript = null;\n" +
            "window.currentDelayExecuteNewScript = null;\n" +
            "\n" +
            "Array.from(document.scripts).forEach((script) => {\n" +
            "    let scriptData = {\n" +
            "        original: script,\n" +
            "        parent: null,\n" +
            "    };\n" +
            "\n" +
            "    if (script.defer) {\n" +
            "        window.scriptsToDeferExecute.push(scriptData)\n" +
            "    } else {\n" +
            "        window.scriptsToExecute.push(scriptData);\n" +
            "    }\n" +
            "});\n" +
            "\n" +
            "window.executeNextScript = function() {\n" +
            "    window.scriptsToExecute.push(...newScriptsToExecute);\n" +
            "    window.scriptsToExecute.push(...scriptsToDeferExecute);\n" +
            "    window.scriptsToDeferExecute = []\n" +
            "    window.newScriptsToExecute = []\n" +
            "\n" +
            "    if (window.scriptsToExecute.length === 0) {\n" +
            "        return [\"\", \"\", \"false\"];\n" +
            "    }\n" +
            "\n" +
            "    let scriptData = window.scriptsToExecute.shift();\n" +
            "    let oldScript = scriptData.original;\n" +
            "\n" +
            "    let newScript = document.createElement('script');\n" +
            "\n" +
            "    let scriptType = '';\n" +
            "    let matching = '';\n" +
            "\n" +
            "    if (oldScript.text) {\n" +
            "            newScript.text = oldScript.text;\n" +
            "            newScript.dataset.observerChangeScript = 'true';\n" +
            "            scriptType = 'internal';\n" +
            "            matching = newScript.text;\n" +
            "    }\n" +
            "\n" +
            "    if (oldScript.src) {\n" +
            "            newScript.src = oldScript.src;\n" +
            "            newScript.dataset.observerChangeScript = 'true';\n" +
            "            scriptType = 'external';\n" +
            "            matching = newScript.src;\n" +
            "    }\n" +
            "\n" +
            "    oldScript.parentNode.insertBefore(newScript, oldScript);\n" +
            "    oldScript.parentNode.removeChild(oldScript);\n" +
            "\n" +
            "    window.currentDelayExecuteNewScript = newScript;\n" +
            "    window.currentDelayExecuteOldScript = oldScript;\n" +
            "\n" +
            "    return [scriptType, matching, \"true\"];\n" +
            "}\n" +
            "\n";

    public static final String ELEMENT_OBSERVER = "\n" +
            "let preVersionElements = new Map();\n" +
            "window.numberOfElement = 0;\n" +
            "window.getElement = function() {\n" +
            "    return window.preVersionElements;\n" +
            "}\n" +
            "\n" +
            "window.findNodeByPath = function(path) {\n" +
            "    Array.from(preVersionElements).forEach(([node, info]) => {\n" +
            "        if (info.path === path) {\n" +
            "            return node;\n" +
            "        }\n" +
            "    });\n" +
            "}\n" +
            "\n" +
            "window.findHTMLNodeListByCSSSelector = function (cssSelector) {\n" +
            "    let pathList = [];\n" +
            "    let nodeList = [];\n" +
            "\n" +
            "    try {\n" +
            "        nodeList = document.querySelectorAll(cssSelector);\n" +
            "    } catch (error) {\n" +
            "        console.error(\"Error querying elements:\", error);\n" +
            "        return pathList;\n" +
            "    }\n" +
            "    Array.from(nodeList).forEach((node) => {\n" +
            "        let info = preVersionElements.get(node);\n" +
            "        if (info != null) {\n" +
            "            let p = info.path;\n" +
            "            pathList.push(p);\n" +
            "        }\n" +
            "    });\n" +
            "    return pathList;\n" +
            "}\n" +
            "\n" +
            "function obtainElements(node, map, path) {\n" +
            "    let info = {\n" +
            "        attribute : \"\",\n" +
            "        path : path,\n" +
            "    };\n" +
            "    if (node.nodeType === Node.ELEMENT_NODE) {\n" +
            "        info.attribute = extractAttribute(node);\n" +
            "    }\n" +
            "    map.set(node, info);\n" +
            "\n" +
            "    Array.from(node.children).forEach((child, index) => {\n" +
            "        obtainElements(child, map, path+'>'+index);\n" +
            "    });\n" +
            "\n" +
            "    if (node.shadowRoot) {\n" +
            "        obtainElements(node.shadowRoot, map, path+'>shadowRoot');\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "obtainElements(document.documentElement, preVersionElements, '0');\n" +
            "\n" +
            "window.compareElements = function() {\n" +
            "    let newVersionElements = new Map();\n" +
            "    let newList = [];\n" +
            "    let changeList = [];\n" +
            "    // The list here should save the path in new version html page\n" +
            "    getAndCompare(document.documentElement, newVersionElements, newList, changeList, '0');\n" +
            "\n" +
            "    let differentList = [];\n" +
            "\n" +
            "    // The node with path in list should be:\n" +
            "    // delete (from bottom to top) use the path from old script, add (from top to bottom) use the path from new script\n" +
            "    // change use the path from new script.\n" +
            "    differentList.push(...newList);\n" +
            "    differentList.push(...changeList);\n" +
            "\n" +
            "    processDelete(differentList);\n" +
            "\n" +
            "    preVersionElements = newVersionElements;\n" +
            "    window.numberOfElement = preVersionElements.size;\n" +
            "    return differentList;\n" +
            "}\n" +
            "\n" +
            "function processDelete(list) {\n" +
            "    Array.from(preVersionElements.keys()).forEach((node) => {\n" +
            "        if (node !== window.currentDelayExecuteOldScript) {\n" +
            "            let nodeData = {\n" +
            "                type : 'node',\n" +
            "                op : 'delete',\n" +
            "                path : preVersionElements.get(node).path,\n" +
            "            }\n" +
            "            if (node.nodeType === Node.DOCUMENT_FRAGMENT_NODE) {\n" +
            "                nodeData.type = 'shadowRoot';\n" +
            "            }\n" +
            "\n" +
            "            list.unshift(nodeData);\n" +
            "        }\n" +
            "    });\n" +
            "}\n" +
            "\n" +
            "function getAndCompare(node, newVersionElements, newList, changeList, path) {\n" +
            "    let contains = preVersionElements.has(node);\n" +
            "    let info = {\n" +
            "        attribute : \"\",\n" +
            "        path : path,\n" +
            "    }\n" +
            "\n" +
            "    if (node.nodeType === Node.ELEMENT_NODE) {\n" +
            "        info.attribute = extractAttribute(node);\n" +
            "    }\n" +
            "\n" +
            "    if (!contains) {\n" +
            "        if (node !== window.currentDelayExecuteNewScript) {\n" +
            "            let nodeData = {\n" +
            "                type: 'node',\n" +
            "                op: 'create',\n" +
            "                path: path,\n" +
            "                node: node,\n" +
            "            }\n" +
            "            if (node.nodeType === Node.DOCUMENT_FRAGMENT_NODE) {\n" +
            "                nodeData.type = 'shadowRoot';\n" +
            "            }\n" +
            "            newList.push(nodeData);\n" +
            "        }\n" +
            "    } else {\n" +
            "        if (node !== window.currentDelayExecuteNewScript && node !== window.currentDelayExecuteOldScript) {\n" +
            "            if (node.nodeType === Node.ELEMENT_NODE) {\n" +
            "                let oldAttribute = preVersionElements.get(node).attribute;\n" +
            "                if (compareAttributes(oldAttribute, info.attribute)) {\n" +
            "                    let nodeData = {\n" +
            "                        type: 'node',\n" +
            "                        op: 'change',\n" +
            "                        path: path,\n" +
            "                        node: node,\n" +
            "                    }\n" +
            "                    changeList.push(nodeData);\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        preVersionElements.delete(node);\n" +
            "    }\n" +
            "\n" +
            "    newVersionElements.set(node, info);\n" +
            "\n" +
            "    Array.from(node.children).forEach((child, index) => {\n" +
            "        getAndCompare(child, newVersionElements, newList, changeList, path+'>'+index);\n" +
            "    });\n" +
            "\n" +
            "    if (node.shadowRoot) {\n" +
            "        getAndCompare(node.shadowRoot, newVersionElements, newList, changeList, path+'>shadowRoot');\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "function extractAttribute(node) {\n" +
            "    let attrs = node.attributes;\n" +
            "    let items = {};\n" +
            "    for (let index = 0; index < attrs.length; index++) {\n" +
            "        items[attrs[index].name] = attrs[index].value\n" +
            "    }\n" +
            "    return items;\n" +
            "}\n" +
            "\n" +
            "function compareAttributes(oldAttribute, newAttribute) {\n" +
            "    if (Object.keys(oldAttribute).length !== Object.keys(newAttribute).length) {\n" +
            "        return true;\n" +
            "    }\n" +
            "\n" +
            "    for (let key in oldAttribute) {\n" +
            "        if (oldAttribute.hasOwnProperty(key)) {\n" +
            "            if (newAttribute[key] === undefined || oldAttribute[key] !== newAttribute[key]) {\n" +
            "                return true;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    return false;\n" +
            "}";
}
