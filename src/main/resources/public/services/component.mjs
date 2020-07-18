import {app} from '../app/app.mjs'
import {removeNamespace, namespaceOnly, withRetry} from '/modules/windflowUtils.mjs'

export const COMPONENT_TYPES = {
    default: Symbol('Identifier for regular components'),
    layout: Symbol('Identifier for layout components'),
};

const endpoints = {
    [COMPONENT_TYPES.default]: '/api/components',
    [COMPONENT_TYPES.layout]: '/api/layouts',
};

function assembleUrl({ namespacedName, type }) {
    const endpoint = endpoints[type];
    const parts = [endpoint, namespaceOnly(namespacedName), `${removeNamespace(namespacedName)}.mjs`];

    return parts.join('/');
}

const importWithRetry = withRetry(url => import(url));
const registeredComponents = {};

export const load = async (namespacedName, {
  logger = console,
  notifier = null,
  type = COMPONENT_TYPES.default,
} = {}) => {
    try {
        const name = removeNamespace(namespacedName);
        if (registeredComponents[name]) return name;

        const url = assembleUrl({ namespacedName, type });
        const module = await importWithRetry(url);

        app.component(name, module.default);
        registeredComponents[name] = true;

        return name;
    } catch (error) {
      if (logger) logger.error(error);
      if (notifier) notifier.notify({ title: error.message });
    }
}
