
let preVersionElements = new Map();
window.numberOfElement = 0;
window.getElement = function() {
    return window.preVersionElements;
}

window.findNodeByPath = function(path) {
    Array.from(preVersionElements).forEach(([node, info]) => {
        if (info.path === path) {
            return node;
        }
    });
}

window.findHTMLNodeListByCSSSelector = function (cssSelector) {
    let pathList = [];
    let nodeList = [];

    try {
        nodeList = document.querySelectorAll(cssSelector);
    } catch (error) {
        console.error("Error querying elements:", error);
        return pathList;
    }
    Array.from(nodeList).forEach((node) => {
        let info = preVersionElements.get(node);
        if (info != null) {
            let p = info.path;
            pathList.push(p);
        }
    });
    return pathList;
}

function obtainElements(node, map, path) {
    let info = {
        attribute : "",
        path : path,
    };
    if (node.nodeType === Node.ELEMENT_NODE) {
        info.attribute = extractAttribute(node);
    }
    map.set(node, info);

    Array.from(node.children).forEach((child, index) => {
        obtainElements(child, map, path+'>'+index);
    });

    if (node.shadowRoot) {
        obtainElements(node.shadowRoot, map, path+'>shadowRoot');
    }
}

obtainElements(document.documentElement, preVersionElements, '0');

window.compareElements = function() {
    let newVersionElements = new Map();
    let newList = [];
    let changeList = [];
    // The list here should save the path in new version html page
    getAndCompare(document.documentElement, newVersionElements, newList, changeList, '0');

    let differentList = [];

    // The node with path in list should be:
    // delete (from bottom to top) use the path from old script, add (from top to bottom) use the path from new script
    // change use the path from new script.
    differentList.push(...newList);
    differentList.push(...changeList);

    processDelete(differentList);

    preVersionElements = newVersionElements;
    window.numberOfElement = preVersionElements.size;
    return differentList;
}

function processDelete(list) {
    Array.from(preVersionElements.keys()).forEach((node) => {
        if (node !== window.currentDelayExecuteOldScript) {
            let nodeData = {
                type : 'node',
                op : 'delete',
                path : preVersionElements.get(node).path,
            }
            if (node.nodeType === Node.DOCUMENT_FRAGMENT_NODE) {
                nodeData.type = 'shadowRoot';
            }

            list.unshift(nodeData);
        }
    });
}

function getAndCompare(node, newVersionElements, newList, changeList, path) {
    let contains = preVersionElements.has(node);
    let info = {
        attribute : "",
        path : path,
    }

    if (node.nodeType === Node.ELEMENT_NODE) {
        info.attribute = extractAttribute(node);
    }

    if (!contains) {
        if (node !== window.currentDelayExecuteNewScript) {
            let nodeData = {
                type: 'node',
                op: 'create',
                path: path,
                node: node,
            }
            if (node.nodeType === Node.DOCUMENT_FRAGMENT_NODE) {
                nodeData.type = 'shadowRoot';
            }
            newList.push(nodeData);
        }
    } else {
        if (node !== window.currentDelayExecuteNewScript && node !== window.currentDelayExecuteOldScript) {
            if (node.nodeType === Node.ELEMENT_NODE) {
                let oldAttribute = preVersionElements.get(node).attribute;
                if (compareAttributes(oldAttribute, info.attribute)) {
                    let nodeData = {
                        type: 'node',
                        op: 'change',
                        path: path,
                        node: node,
                    }
                    changeList.push(nodeData);
                }
            }
        }
        preVersionElements.delete(node);
    }

    newVersionElements.set(node, info);

    Array.from(node.children).forEach((child, index) => {
        getAndCompare(child, newVersionElements, newList, changeList, path+'>'+index);
    });

    if (node.shadowRoot) {
        getAndCompare(node.shadowRoot, newVersionElements, newList, changeList, path+'>shadowRoot');
    }
}

function extractAttribute(node) {
    let attrs = node.attributes;
    let items = {};
    for (let index = 0; index < attrs.length; index++) {
        items[attrs[index].name] = attrs[index].value
    }
    return items;
}

function compareAttributes(oldAttribute, newAttribute) {
    if (Object.keys(oldAttribute).length !== Object.keys(newAttribute).length) {
        return true;
    }

    for (let key in oldAttribute) {
        if (oldAttribute.hasOwnProperty(key)) {
            if (newAttribute[key] === undefined || oldAttribute[key] !== newAttribute[key]) {
                return true;
            }
        }
    }

    return false;
}