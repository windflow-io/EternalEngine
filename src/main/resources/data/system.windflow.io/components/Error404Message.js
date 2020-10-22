export default {
    name: 'OurComponent',
    props: {
        heading: {
            default: 'Hello World',
            type: String,
        },
        paragraph: {
            default: 'Lorem Ipsum',
            type: String,
        },
    },
    schema: {
        heading: {
            type: 'text',
            label: 'Heading',
        },
        paragraph: {
            type: 'textarea',
            label: 'Paragraph',
        },
    },
    template: `
    <div class="max-w-screen-xl mx-auto py-12 px-4 sm:px-6 lg:py-16 lg:px-8">
        <h2 class="leading-9 font-extrabold tracking-tight text-white sm:leading-10 text-3xl">
            {{ heading }}
        </h2>
        <p class="mt-2 text-base text-gray-500 sm:mt-5 sm:text-lg sm:max-w-xl sm:mx-auto md:mt-5 md:text-xl lg:mx-0">
            {{ paragraph }}
        </p>
    </div>
`,
}
