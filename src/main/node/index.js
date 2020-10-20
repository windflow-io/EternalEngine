const {
    compileScript,
    compileTemplate,
    parse,
    rewriteDefault,
} = require('@vue/compiler-sfc');
const { minify } = require('terser');
const fastify = require('fastify');

const PORT = 3000;
const PATH = '/services';
const VUE_PATH = '/vendor/vue3/vue.esm-browser.js';

const compileComponent = async ({ name, sfc }) => {
    const filename = `${name}.vue`;
    const { descriptor, errors } = parse(sfc, { filename });

    if (errors.length) throw new Error('Error compiling SFC');

    const codeParts = [];

    if (descriptor.script || descriptor.scriptSetup) {
        const script = compileScript(descriptor);
        codeParts.push(rewriteDefault(script.content, '__script'));
    } else {
        codeParts.push(`const __script = { name: '${name}' }`);
    }

    if (descriptor.template) {
        const template = compileTemplate({
            filename,
            source: descriptor.template.content,
        });
        let templateCode = template.code.replace('from "vue"', `from '${VUE_PATH}'`);
        templateCode = templateCode.replace('export function render', 'function __render');

        codeParts.push(templateCode);
        codeParts.push(`__script.render = __render`);
    }

    codeParts.push(`export default __script`);

    const { code } = await minify(codeParts.join('\n'));

    return code;
};

const server = fastify({
    logger: true
});

server.post(`${PATH}/compile-vue-sfc`, async (request, reply) => {
    reply.type('application/json').code(200);

    const { name, sfc } = request.body;
    const code = await compileComponent({ name, sfc });

    return { code, name, sfc };
});

server.listen(PORT, (error, address) => {
    if (error) throw error;
    server.log.info(`server listening on ${address}`);
});
