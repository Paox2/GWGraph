/**
 * Count the number of html element (include shadow) in current page.
 *
 * @param node
 * @returns {number}
 */
function countElements(node) {
    let count = 0;

    if (node.nodeType === Node.ELEMENT_NODE) {
        count += 1;
    }

    Array.from(node.children).forEach(child => {
        count += countElements(child);
    });

    if (node.shadowRoot) {
        count += countElements(node.shadowRoot);
    }

    return count;
}
countElements(document.documentElement);

/**
 * Find all shadow roots.
 *
 * @param root
 * @param roots
 * @returns {*[]}
 */
function findShadowRoots(root = document.documentElement, roots = []) {
    root.querySelectorAll('*').forEach(elem => {
        if (elem.shadowRoot) {
            roots.push(elem.shadowRoot);
            findShadowRoots(elem.shadowRoot, roots);
        }
    });
    return roots;
}
findShadowRoots();