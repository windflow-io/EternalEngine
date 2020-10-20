    import {
    computed,
    getCurrentInstance,
    inject,
    onUnmounted,
    onUpdated,
    ref,
    watchEffect,
} from '../vendor/vue3/vue.esm-browser.js';
import {
    CONTEXT_COMPONENT_REGISTRY,
    CONTEXT_EDIT_MODE,
    CONTEXT_ROUTER,
} from '../app/app.mjs';
import {
    arrayMoveIndex,
    loadEditor,
    useDrag,
} from './windflowUtils.mjs';

// REFACTOR
import '../vendor/popperjs/popperjs-2.5.3.js';

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
                :model-value="modelValue && modelValue[fieldName]"
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
            default: null,
            type: Object,
        },
    },
    template: `
        <flow-form-fields
            v-if="schema"
            :fields="schema"
            :model-value="modelValue"
        />
        <p v-else>
            This component has no schema.
        </p>
    `,
};

export const FlowToolbar = {
    name: 'FlowToolbar',
    components: {
        CodeEditor,
        FlowIcon,
        FlowToolbarForm,
    },
    props: {
        chapter: {
            default: null,
            type: Object,
        },
    },
    setup(props) {
        const { components } = inject(CONTEXT_COMPONENT_REGISTRY);
        const component = computed(() => props.chapter && components[props.chapter.component.id]);

        const tab = ref(null);
        const $root = ref(null);
        const { startDrag } = useDrag({ $el: $root });

        return {
            $root,
            component,
            startDrag,
            tab,
        };
    },
    template: `
        <div
            class="fixed z-50 bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all text-gray-700 max-w-xl"
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
                    v-if="!chapter"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-300 hover:text-gray-800"
                    title="add component"
                    aria-label="add component"
                >
                    <flow-icon icon="plus" class="text-md" />
                </button>
                <button
                    v-if="chapter"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-300 hover:text-gray-800"
                    title="edit chapter data"
                    aria-label="edit chapter data"
                    @click="tab = 'edit-chapter-data'"
                >
                    <flow-icon icon="edit" class="text-md" />
                </button>
                <button
                    v-if="chapter"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-r border-gray-300 hover:text-gray-800"
                    title="edit chapter component"
                    aria-label="edit chapter component"
                    @click="tab = 'edit-chapter-component'"
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
                    @click="$emit('save-page')"
                >
                    <flow-icon icon="save" class="text-md"/>
                </button>
                <button
                    v-if="chapter"
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-gray-300 hover:text-gray-800"
                    title="exit chapter edit mode"
                    aria-label="exit chapter edit mode"
                    @click="$emit('disable-chapter-edit-mode')"
                >
                    <flow-icon icon="times" class="text-md" />
                </button>
                <button
                    v-else
                    class="flex items-center pl-5 pr-5 mt-3 mb-3 border-gray-300 hover:text-gray-800"
                    title="exit edit mode"
                    aria-label="exit edit mode"
                    @click="$emit('disable-edit-mode')"
                >
                    <flow-icon icon="times" class="text-md" />
                </button>
            </div>
            <div
                v-if="chapter"
                class="bg-gray-100 px-4 py-3 text-xs"
            >
                {{ chapter.component.name }}
            </div>
            <div
                v-if="tab"
                class="w-full"
            >
                <div
                    v-if="tab === 'edit-chapter-data' && component"
                    class="p-6 bg-white"
                    style="max-height: 42rem;overflow: auto;"
                >
                    <flow-toolbar-form
                        :schema="component.schema"
                        :model-value="chapter.data"
                        class="w-full h-full"
                        @update:model-value="$emit('update-chapter', { ...chapter, data: $event })"
                    />
                </div>
                <code-editor
                    v-if="tab === 'edit-chapter-component' && chapter"
                    :value="chapter.component.sfc"
                    class="w-full h-full"
                    @save="$emit('save-component', { ...chapter.component, sfc: $event })"
                />
            </div>
        </div>
    `,
}

export const FlowAreaChapter = {
    name: 'FlowAreaChapter',
    props: {
        component: {
            required: true,
            type: Object,
        },
        data: {
            default: null,
            type: Object,
        },
        hasOverlay: {
            default: false,
            type: Boolean,
        },
        isFirst: {
            default: false,
            type: Boolean,
        },
        isLast: {
            default: false,
            type: Boolean,
        },
    },
    setup(props) {
        const { components } = inject(CONTEXT_COMPONENT_REGISTRY);
        const renderComponent = computed(() => components[props.component.id]);

        const $overlay = ref(null);
        const $root = ref(null);

        // REFACTOR
        // Only load and initialize popper when in edit mode.
        const overlay = {
            name: 'overlay',
            enabled: true,
            phase: 'beforeWrite',
            requires: ['computeStyles'],
            fn: ({ state }) => {
              state.styles.popper.width = `${state.rects.reference.width}px`;
              state.styles.popper.height = `${state.rects.reference.height}px`;
              state.styles.popper.marginTop = `-${state.rects.reference.height}px`;
            },
            effect: ({ state }) => {
              state.elements.popper.style.width = `${state.elements.reference.offsetWidth}px`;
              state.elements.popper.style.height = `${state.elements.reference.offsetHeight}px`;
              state.elements.popper.style.marginTop = `-${state.elements.reference.offsetHeight}px`;
            }
        };

        let popperInstance = null;
        watchEffect(() => {
            if (!$overlay.value) return;

            popperInstance = Popper.createPopper($root.value, $overlay.value, {
                modifiers: [
                    {
                        name: 'flip',
                        enabled: false,
                    },
                    overlay,
                ],
                placement: 'bottom-end',
            });
        });

        onUnmounted(() => {
            if (!popperInstance) return;
            popperInstance.destroy();
        });

        onUpdated(() => {
            if (!popperInstance) return;
            popperInstance.forceUpdate();
        });

        return {
            $overlay,
            $root,
            renderComponent,
        };
    },
    template: `
        <div ref="$root">
            <div
                v-if="hasOverlay"
                ref="$overlay"
                class="opacity-0 hover:opacity-100 hover:bg-blue-700 hover:bg-opacity-50 transition duration-200 z-40"
            >
                <div class="inline-flex bg-blue-700 text-white leading-none text-xs">
                    <div class="p-2 border-r border-white">
                        {{ renderComponent && renderComponent.name }}
                    </div>
                    <button
                        v-if="!isFirst"
                        class="p-2 border-r border-white"
                        @click="$emit('move-up')"
                    >
                        Up
                    </button>
                    <button
                        v-if="!isLast"
                        class="p-2"
                        @click="$emit('move-down')"
                    >
                        Down
                    </button>
                </div>
            </div>
            <component
                :is="renderComponent"
                v-bind="data"
            />
        </div>
    `,
}

export const FlowArea = {
    name: 'FlowArea',
    components: {
        FlowAreaChapter,
        FlowButton,
        FlowFormFieldText,
    },
    props: {
        chapters: {
            required: true,
            type: Array,
        },
        name: {
            required: true,
            type: String,
        },
    },
    setup(props) {
        const { isInEditMode } = inject(CONTEXT_EDIT_MODE);
        const newComponentName = ref(null);
        const newChapter = computed(() => ({
            component: {
                name: newComponentName.value,
            },
        }));
        const chapterIds = computed(() => props.chapters.map(chapter => chapter.id));

        return {
            arrayMoveIndex,
            chapterIds,
            isInEditMode,
            newChapter,
            newComponentName,
        };
    },
    template: `
        <!--
          Emitting events directly on the parent component (layout) so layouts
          don't have to pass through the event which makes it easier for our
          users to create their own layouts without knowing about this
          implementation detail.
        -->
        <div>
            <flow-area-chapter
                v-for="(chapter, index) in chapters"
                :key="chapter.id"
                :component="chapter.component"
                :data="chapter.data"
                :has-overlay="isInEditMode"
                :is-first="index === 0"
                :is-last="index === chapters.length - 1"
                @click="isInEditMode && $parent.$emit('enable-chapter-edit-mode', chapter)"
                @move-down="$parent.$emit('reorder-chapters', name, arrayMoveIndex(chapterIds, index, index + 1))"
                @move-up="$parent.$emit('reorder-chapters', name, arrayMoveIndex(chapterIds, index, index - 1))"
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
                <flow-button @click="$parent.$emit('add-chapter', name, newChapter)">
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
