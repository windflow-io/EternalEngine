const { PurgeCSS } = require('purgecss');
const { assemble, createDefaultCompiler } = require('@vue/component-compiler');

const purge = async ({ content, css }) => {
    return new PurgeCSS().purge({
        content: [
            {
                extension: 'vue',
                raw: content,
            },
        ],
        css: [
            {
                raw: css,
            },
        ],
    })
};

const sfcCompiler = createDefaultCompiler();

const compileComponent = async ({ component, css }) => {
    const purgedCss = await purge({ content: component.sfc, css });
    const sfc = sfcCompiler.compileToDescriptor(component.fileName, component.sfc);
    const { code, map } = assemble(sfcCompiler, component.fileName, sfc);
    console.log(code);
    console.log(purgedCss);
};

const componentString = `
<template>
    <div class="classA classB space-y-0" :class="classVarC">
        Hello World
    </div>
</template>

<script>
export default {
    name: 'TestComponenet',
    created() {
        this.classVarC = 'classC';
    },
};
</script>
`;

const tailwindString = `.classA {}.classB {}.classC {}.classD {}.space-y-0>:not(template)~:not(template){--space-y-reverse:0;margin-top:calc(0px * calc(1 - var(--space-y-reverse)));margin-bottom:calc(0px * var(--space-y-reverse))}`;

compileComponent({
    component: {fileName: 'TestComponent.vue', sfc: componentString },
    css: tailwindString,
});
