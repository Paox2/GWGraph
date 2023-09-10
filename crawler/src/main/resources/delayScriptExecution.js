window.scriptsToExecute = [];
window.scriptsToDeferExecute = [];
window.newScriptsToExecute = [];

window.currentDelayExecuteOldScript = null;
window.currentDelayExecuteNewScript = null;

Array.from(document.scripts).forEach((script) => {
    let scriptData = {
        original: script,
        parent: null,
    };

    script.dataset.observerChangeScript = 'true';

    if (script.defer) {
        window.scriptsToDeferExecute.push(scriptData)
    } else {
        window.scriptsToExecute.push(scriptData);
    }
});

window.executeNextScript = function() {
    window.scriptsToExecute.push(...newScriptsToExecute);
    window.scriptsToExecute.push(...scriptsToDeferExecute);
    window.scriptsToDeferExecute = []
    window.newScriptsToExecute = []

    if (window.scriptsToExecute.length === 0) {
        return ["", "", "false"];
    }

    let scriptData = window.scriptsToExecute.shift();
    let oldScript = scriptData.original;

    let newScript = document.createElement('script');

    let scriptType = '';
    let matching = '';

    if (oldScript.text) {
        newScript.text = oldScript.text;
        newScript.dataset.observerChangeScript = 'true';
        scriptType = 'internal';
        matching = newScript.text;
    }

    if (oldScript.src) {
        newScript.src = oldScript.src;
        newScript.dataset.observerChangeScript = 'true';
        scriptType = 'external';
        matching = newScript.src;
    }

    oldScript.parentNode.insertBefore(newScript, oldScript);
    oldScript.parentNode.removeChild(oldScript);

    window.currentDelayExecuteNewScript = newScript;
    window.currentDelayExecuteOldScript = oldScript;

    return [scriptType, matching, "true"];
}

