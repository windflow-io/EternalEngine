import {
    computed,
    getCurrentInstance,
    inject,
    ref,
} from '../vendor/vue3/vue.esm-browser.js';
import {
    CONTEXT_CACHE_COMPONENT,
    CONTEXT_CACHE_PAGE,
    CONTEXT_COMPONENT_REGISTRY,
    CONTEXT_EDIT_MODE,
    CONTEXT_ROUTER,
} from '../app/app.mjs';
import {
    loadEditor,
    useDrag,
} from './windflowUtils.mjs';

export const FlowButton = {
    name: 'FlowButton',
    props: {
        tag: {
            default: 'button',
            type: String,
        },
    },
    template: `
        <component
            :is="tag"
            type="button"
            class="px-6 py-3 border border-transparent text-base leading-6 font-medium rounded-md transition duration-150 ease-in-out text-white bg-indigo-600 hover:bg-indigo-500 focus:outline-none focus:border-indigo-700 focus:shadow-outline-indigo"
        >
            <slot/>
        </component>
    `,
}

export const FlowIcon = {
    name: 'FlowIcon',
    props: ['icon'],
    data() {
        return {
            viewBox: '0 0 0 0'
        }
    },
    template: `
        <svg
            xmlns="http://www.w3.org/2000/svg"
            :viewBox="viewBox"
            ref="svgTag"
            fill="currentColor"
            style="width:auto;height:1em;"
        />
    `,
    mounted() {

        let typeAndIcon = this.icon.split(" ");
        let type = 'solid'
        let name = ''
        if ((typeAndIcon.length) > 1) {
            type = typeAndIcon[0];
            name = typeAndIcon[1];
        } else {
            name = typeAndIcon[0];
        }
        fetch('/icons/' + type + '/' + name + '.svg').then(r => r.text()).then(d => {
            let svg = new DOMParser().parseFromString(d, "image/svg+xml").firstChild;
            this.viewBox = svg.getAttribute('viewBox');
            svg.childNodes.forEach(n => {
                this.$refs.svgTag.append(n);
            })
        })
    }
}

export const CodeEditor = {
  name: 'CodeEditor',
  model: {
    // REFACTOR
    // - Model value.
    prop: 'value',
    event: 'update',
  },
  props: {
    options: {
        default: () => ({
            language: 'javascript',
        }),
        type: Object,
    },
    value: {
        default: '',
        type: String,
    },
  },
  data() {
    return {
      loading: true,
    };
  },
  mounted() {
    this.init();
  },
  watch: {
    value() {
        if (this.value === this.editor.getValue()) return;

        this.editor.setValue(this.value);
    }
  },
  methods: {
    async init() {
        const monaco = await loadEditor();
        this.loading = false;
        // Wait until re-render so that the editor container has correct dimensions.
        await this.$nextTick();
        this.editor = monaco.editor.create(this.$refs.editor, {
            ...this.options,
            value: this.value,
        });
    },
  },
  template: `
    <div>
        <div v-if="loading">
            LOADING ...
        </div>
        <div v-else>
            <div
                style="width:800px;height:600px;max-width:100%;max-height:100%;"
                ref="editor"
                @input="$emit('update', this.editor.getValue())"
            />
            <div class="px-4 py-2">
                <button
                    class="px-4 py-1 border border-transparent text-base leading-6 font-medium rounded-md transition duration-150 ease-in-out text-white bg-indigo-600 hover:bg-indigo-500 focus:outline-none focus:border-indigo-700 focus:shadow-outline-indigo"
                    @click="$emit('save', this.editor.getValue())"
                >
                    Save
                </button>
            </div>
        </div>
    </div>
  `,
}

export const FlowFormGroup = {
    name: 'FlowFormGroup',
    props: {
        label: {
            required: true,
            type: String,
        },
        name: {
            required: true,
            type: String,
        },
    },
    setup(props) {
        const id = computed(() => `${props.name}-${getCurrentInstance().uid}`);

        return { id };
    },
    template: `
        <div>
            <label
                :for="id"
                class="block text-sm leading-5 font-medium text-gray-700"
            >
                {{ label }}
            </label>
            <div class="mt-1">
                <slot :id="id"/>
            </div>
        </div>
    `,
}

export const FlowFormFieldText = {
    name: 'FlowFormFieldText',
    components: {
        FlowFormGroup,
    },
    props: {
        label: {
            required: true,
            type: String,
        },
        modelValue: {
            default: '',
            type: [Number, String],
        },
        name: {
            required: true,
            type: String,
        },
    },
    template: `
        <flow-form-group
            :label="label"
            :name="name"
            #default="{ id }"
        >
            <input
                :id="id"
                :value="modelValue"
                :name="name"
                class="border border-gray-400 block w-full p-3 leading-6 rounded-md shadow-sm"
                @input.stop="$emit('update:modelValue', $event.target.value)"
            >
        </flow-form-group>
    `,
}

export const FlowFormFieldTextarea = {
    name: 'FlowFormFieldTextarea',
    components: {
        FlowFormGroup,
    },
    props: {
        label: {
            required: true,
            type: String,
        },
        modelValue: {
            default: '',
            type: String,
        },
        name: {
            required: true,
            type: String,
        },
    },
    template: `
        <flow-form-group
            :label="label"
            :name="name"
            #default="{ id }"
        >
            <textarea
                :id="id"
                :value="modelValue"
                :name="name"
                class="border border-gray-400 block w-full p-3 h-24 sm:text-sm sm:leading-5 rounded-md shadow-sm"
                @input.stop="$emit('update:modelValue', $event.target.value)"
            />
        </flow-form-group>
    `,
}

export const FlowFormFieldSelect = {
    name: 'FlowFormFieldSelect',
    components: {
        FlowFormGroup,
    },
    props: {
        label: {
            required: true,
            type: String,
        },
        modelValue: {
            default: '',
            type: [Number, String],
        },
        name: {
            required: true,
            type: String,
        },
        options: {
            required: true,
            type: Array,
        },
    },
    template: `
        <flow-form-group
            :label="label"
            :name="name"
            #default="{ id }"
        >
            <select
                :id="id"
                :value="modelValue"
                :name="name"
                class="border border-gray-400 block w-full p-3 sm:text-sm sm:leading-5 rounded-md shadow-sm"
                @input.stop="$emit('update:modelValue', $event.target.value)"
            >
                <option
                    v-for="option in options"
                    :key="option"
                    :value="option"
                >
                    {{ option }}
                </option>
            </select>
        </flow-form-group>
    `,
}

const fieldMap = {
    select: FlowFormFieldSelect,
    text: FlowFormFieldText,
    textarea: FlowFormFieldTextarea,
};

export const FlowFormFields = {
    name: 'FlowFormFields',
    props: {
        fields: {
            required: true,
            type: Object,
        },
        modelValue: {
            default: () => ({}),
            type: Object,
        },
    },
    computed: {
        fieldNames() {
            return Object.keys(this.fields);
        }
    },
    created() {
        this.fieldMap = fieldMap;
    },
    methods: {
        update(fieldName, fieldValue) {
            const newValue = {
                ...this.modelValue,
                [fieldName]: fieldValue,
            };
            this.$emit('update:modelValue', newValue);
        },
    },
    template: `
        <div class="space-y-6">
            <component
                v-for="fieldName in fieldNames"
                :key="fieldName"
                :is="fieldMap[fields[fieldName].type]"
                v-bind="fields[fieldName]"
                :name="fieldName"
                :model-value="modelValue[fieldName]"
                @update:model-value="update(fieldName, $event)"
            />
        </div>
    `,
};

export const FlowFormFieldFieldset = {
    name: 'FlowFormFieldFieldset',
    components: {
        FlowFormFields,
    },
    props: {
        fields: {
            required: true,
            type: Object,
        },
        label: {
            required: true,
            type: String,
        },
        modelValue: {
            default: () => ({}),
            type: Object,
        },
    },
    template: `
        <div>
            <div>{{ label }}</div>
            <div class="mt-1 p-4 border border-gray-300">
                <flow-form-fields
                    :fields="fields"
                    :model-value="modelValue"
                    @update:model-value="$emit('update:modelValue', $event)"
                />
            </div>
        </div>
    `,
};

fieldMap.fieldset = FlowFormFieldFieldset;

export const FlowToolbarForm = {
    name: 'FlowToolbarForm',
    components: {
        FlowFormFields,
    },
    props: {
        modelValue: {
            default: () => ({}),
            type: Object,
        },
        schema: {
            required: true,
            type: Object,
        },
    },
    template: `
        <flow-form-fields
            :fields="schema"
            :model-value="modelValue"
        />
    `,
};

export const FlowToolbar = {
    name: 'FlowToolbar',
    components: {
        CodeEditor,
        FlowIcon,
        FlowToolbarForm,
    },
    setup() {
        const {
            activeAreaComponent,
            disableEditMode,
            editAreaComponent,
        } = inject(CONTEXT_EDIT_MODE);
        const isInEditComponentMode = computed(() => !!activeAreaComponent.value);

        const areaComponentId = computed(() => activeAreaComponent.value && activeAreaComponent.value.id);
        const areaComponentName = computed(() => activeAreaComponent.value && activeAreaComponent.value.name);

        const { components } = inject(CONTEXT_COMPONENT_REGISTRY);
        const component = computed(() => components[areaComponentName.value]);

        const {
            loadComponent,
            updateComponent,
        } = inject(CONTEXT_CACHE_COMPONENT);
        const { data: componentCode } = loadComponent(() => ({
            name: areaComponentName.value,
        }));
        const saveCode = data => updateComponent({
            data,
            name: areaComponentName.value,
        });

        const {
            commitComponentData,
            loadComponentData,
            updateComponentData,
        } = inject(CONTEXT_CACHE_PAGE);
        const { currentPath } = inject(CONTEXT_ROUTER);
        const { data: componentData } = loadComponentData(() => ({
            id: areaComponentId.value,
            path: currentPath.value,
        }));
        const commitData = data => commitComponentData({
            data,
            id: areaComponentId.value,
        });
        const savePage = () => updateComponentData({
            path: currentPath.value,
        });

        const tab = ref(null);
        const $root = ref(null);
        const { startDrag } = useDrag({ $el: $root });

        return {
            $root,
            activeAreaComponent,
            commitData,
            component,
            componentCode,
            componentData,
            disableEditMode,
            editAreaComponent,
            isInEditComponentMode,
            saveCode,
            savePage,
            startDrag,
            tab,
        };
    },
    template: `
        <div
            class="fixed z-30 bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all text-gray-700 max-w-xl"
            style="left:calc(100% - 380px);top:50px;"
            ref="$root"
        >
            <div class="flex h-16">
                <button
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-300 hover:text-gray-800 cursor-move"
                    title="drag"
                    aria-label="drag"
                    @mousedown="startDrag"
                >
                    <flow-icon icon="grip-vertical" class="text-md" />
                </button>
                <button
                    v-if="!isInEditComponentMode"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-300 hover:text-gray-800"
                    title="add component"
                    aria-label="add component"
                >
                    <flow-icon icon="plus" class="text-md" />
                </button>
                <button
                    v-if="isInEditComponentMode"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-300 hover:text-gray-800"
                    title="edit component data"
                    aria-label="edit component data"
                    @click="tab = 'edit-component-data'"
                >
                    <flow-icon icon="edit" class="text-md" />
                </button>
                <button
                    v-if="isInEditComponentMode"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-300 hover:text-gray-800"
                    title="edit code"
                    aria-label="edit code"
                    @click="tab = 'edit-code'"
                >
                    <flow-icon icon="code" class="text-md" />
                </button>
                <button
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-300 hover:text-gray-800"
                    title="rollback (coming soon)"
                    aria-label="rollback (coming soon)"
                >
                    <flow-icon icon="undo-alt" class="text-md" />
                </button>
                <button
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-300 hover:text-gray-800"
                    title="save"
                    aria-label="save"
                    @click="savePage"
                >
                    <flow-icon icon="save" class="text-md"/>
                </button>
                <button
                    v-if="isInEditComponentMode"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-gray-300 hover:text-gray-800"
                    title="exit component edit mode"
                    aria-label="exit component edit mode"
                    @click="editAreaComponent(null)"
                >
                    <flow-icon icon="times" class="text-md" />
                </button>
                <button
                    v-else
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-gray-300 hover:text-gray-800"
                    title="exit edit mode"
                    aria-label="exit edit mode"
                    @click="disableEditMode"
                >
                    <flow-icon icon="times" class="text-md" />
                </button>
            </div>
            <div
                v-if="activeAreaComponent"
                class="bg-gray-100 px-4 py-3 text-xs"
            >
                {{ activeAreaComponent.name }}
            </div>
            <div
                v-if="tab"
                class="w-full"
            >
                <div
                    v-if="tab === 'edit-component-data' && component.schema"
                    class="p-6 bg-white"
                    style="max-height: 42rem;overflow: auto;"
                >
                    <flow-toolbar-form
                        :schema="component.schema"
                        :model-value="componentData"
                        class="w-full h-full"
                        @update:model-value="commitData"
                    />
                </div>
                <code-editor
                    v-if="tab === 'edit-code'"
                    :value="componentCode"
                    class="w-full h-full"
                    @save="saveCode"
                />
            </div>
        </div>
    `,
}

export const FlowAreaComponent = {
    name: 'FlowAreaComponent',
    props: {
        areaComponent: {
            required: true,
            type: Object,
        },
    },
    setup(props) {
        const { components } = inject(CONTEXT_COMPONENT_REGISTRY);
        const component = computed(() => components[props.areaComponent.name]);

        const { loadComponentData } = inject(CONTEXT_CACHE_PAGE);
        const { currentPath } = inject(CONTEXT_ROUTER);
        const { data } = loadComponentData(() => ({
            id: props.areaComponent.id,
            path: currentPath.value,
        }));

        const {
            editAreaComponent,
            isInEditMode,
        } = inject(CONTEXT_EDIT_MODE);

        return {
            component,
            data,
            editAreaComponent,
            isInEditMode,
        };
    },
    template: `
        <component
            :key="areaComponent.id"
            :is="component"
            v-bind="data"
            :style="isInEditMode && 'outline: rgba(0, 0, 0, 0.3) dashed 1px;'"
            @click="isInEditMode && editAreaComponent(areaComponent)"
        />
    `,
}

export const FlowArea = {
    name: 'FlowArea',
    components: {
        FlowAreaComponent,
        FlowButton,
        FlowFormFieldText,
    },
    props: {
        areaComponents: {
            required: true,
            type: Array,
        },
        name: {
            required: true,
            type: String,
        },
    },
    setup(props) {
        const { addDefaultComponent } = inject(CONTEXT_CACHE_COMPONENT);
        const { addAreaComponent } = inject(CONTEXT_CACHE_PAGE);
        const { isInEditMode } = inject(CONTEXT_EDIT_MODE);
        const { currentPath } = inject(CONTEXT_ROUTER);

        const newComponentName = ref(null);

        const addComponent = async () => {
            // REFACTOR
            // - Should not have to deal with host here.
            const urlParams = new URLSearchParams(window.location.search);
            const host = urlParams.get('host') || location.host;
            const id = Date.now();
            const name = newComponentName.value;
            await addDefaultComponent({ name });

            const areaComponent = { id, name: `${host}.${name}` };

            return addAreaComponent({
                areaComponent,
                areaName: props.name,
                path: currentPath.value,
            });
        };

        return {
            addComponent,
            isInEditMode,
            newComponentName,
        };
    },
    template: `
        <div>
            <flow-area-component
                v-for="areaComponent in areaComponents"
                :key="areaComponent.id"
                :area-component="areaComponent"
            />
            <div
                v-if="isInEditMode"
                style="outline: rgba(0, 0, 0, 0.3) dashed 1px;"
                class="flex p-8 justify-center items-end"
            >
                <flow-form-field-text
                    v-model="newComponentName"
                    label="Component name"
                    name="new-component-name"
                    class="mr-2"
                />
                <flow-button @click="addComponent">
                    Add new component
                </flow-button>
            </div>
        </div>
    `,
}

export const FlowLink = {
    name: 'FlowLink',
    props: {
        to: {
            required: true,
            type: String,
        },
    },
    setup(props) {
        const { push } = inject(CONTEXT_ROUTER);

        const handleClick = (event) => {
            event.preventDefault();
            push(props.to);
        };

        return {
            handleClick,
        };
    },
    template: `
        <a
            :href="to"
            @click="handleClick"
        >
            <slot/>
        </a>
    `,
}

export const ErrorLayout = {
    name: 'ErrorLayout',
    setup() {
        const { page } = inject(CONTEXT_ROUTER);

        return {
            page,
        };
    },
    template:
        `<div class="flex flex-row h-screen items-center justify-center p-10 bg-gray-900">
            <div class="relative z-10 max-w-4xl opacity-100">
                <section class="flex flex-col">
                    <h1 class="flex text-6xl justify-center font-semibold text-gray-300">
                        {{ page.data.errorTitle }}
                    </h1>
                    <h2 class="flex text-3xl justify-center text-gray-600">
                        {{ page.data.errorDescription }}
                    </h2>
                    <h2 class="flex text-xl justify-center text-gray-700 mt-4">
                        {{ page.data.errorDetail }}
                    </h2>
                </section>
            </div>
        </div>
         `,
}
