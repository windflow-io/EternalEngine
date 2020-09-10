export default {
    name: 'CtaSection',
    props: {
        cta1: {
            default: null,
            type: Object,
        },
        cta2: {
            default: null,
            type: Object,
        },
        heading: {
            default: null,
            type: Object,
        },
        paragraph: {
            default: null,
            type: String,
        },
    },
    schema: {
        heading: {
            label: 'Heading',
            type: 'fieldset',
            fields: {
                text1: {
                    label: 'Text 1',
                    type: 'text',
                },
                text2: {
                    label: 'Text 2',
                    type: 'text',
                },
                level: {
                    label: 'Level',
                    type: 'select',
                    options: [1, 2, 3, 4, 5, 6],
                    default: 2,
                },
            },
        },
        paragraph: {
            label: 'Paragraph',
            type: 'textarea',
        },
        cta1: {
            label: 'CTA Button 1',
            type: 'fieldset',
            fields: {
                text: {
                    label: 'Text',
                    type: 'text',
                },
                link: {
                    label: 'Link',
                    type: 'text',
                },
                style: {
                    label: 'Style',
                    type: 'select',
                    options: ['primary', 'secondary'],
                    default: 'primary',
                },
            },
        },
        cta2: {
            label: 'CTA Button 2',
            type: 'fieldset',
            fields: {
                text: {
                    label: 'Text',
                    type: 'text',
                },
                link: {
                    label: 'Link',
                    type: 'text',
                },
                style: {
                    label: 'Style',
                    type: 'select',
                    options: ['primary', 'secondary'],
                    default: 'primary',
                },
            },
        },
    },
    created() {
        this.primaryStyle = 'text-white bg-indigo-600 hover:bg-indigo-500 focus:outline-none focus:shadow-outline';
        this.secondaryStyle = 'text-indigo-600 bg-white hover:text-indigo-500 focus:outline-none focus:shadow-outline';
    },
    template: `
        <div class="bg-gray-50">
            <div class="max-w-screen-xl mx-auto py-12 px-4 sm:px-6 lg:py-16 lg:px-8 lg:flex lg:items-center lg:justify-between">
            <h2
                class="leading-9 font-extrabold tracking-tight text-gray-900 sm:leading-10"
                :class="{
                    'text-4xl': \`\${heading.level}\` === '1',
                    'text-3xl': \`\${heading.level}\` === '2',
                    'text-2xl': \`\${heading.level}\` === '3',
                    'text-xl': \`\${heading.level}\` === '4',
                    'text-lg': \`\${heading.level}\` === '5',
                }"
            >
                {{ heading.text1 }}
                <br>
                <span class="text-indigo-600">{{ heading.text2 }}</span>
            </h2>
            <div class="mt-8 flex lg:flex-shrink-0 lg:mt-0">
                <div class="inline-flex rounded-md shadow">
                <a
                    :href="cta1.link"
                    class="inline-flex items-center justify-center px-5 py-3 border border-transparent text-base leading-6 font-medium rounded-md transition duration-150 ease-in-out"
                    :class="cta1.style === 'primary' ? primaryStyle : secondaryStyle"
                >
                    {{ cta1.text }}
                </a>
                </div>
                <div class="ml-3 inline-flex rounded-md shadow">
                <a
                    :href="cta2.link"
                    class="inline-flex items-center justify-center px-5 py-3 border border-transparent text-base leading-6 font-medium rounded-md transition duration-150 ease-in-out"
                    :class="cta2.style === 'primary' ? primaryStyle : secondaryStyle"
                >
                    {{ cta2.text }}
                </a>
                </div>
            </div>
            </div>
        </div>
    `
}
