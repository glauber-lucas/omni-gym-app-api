# Welcome to NativeWind Boilerplate

This is an Expo project template with a lot of batteries included to help you get started quickly.

## Get started

1. Install dependencies:

   ```bash
   pnpm install
   ```

2. Create your development build:

   ```bash
   pnpm android
   # or
   pnpm ios
   ```

3. Start the development server:

   ```bash
   pnpm start
   ```

## Important changes to make

1. Change the name in `app.json` and `package.json` (search for `changethisname`).

2. Create the images for the app icon and splash screen in the `assets/expo` folder. You can use [this tool](https://www.figma.com/design/JKBA5gLW6m1NStGIMcQmlQ/Expo-App-Icon---Splash-SDK-54---Community---Community-?node-id=4-868&p=f&t=FzBz9wvjz3yGUJAt-0) to generate the icons.

## Useful commands

- `pnpm typecheck`: Run TypeScript type checking.
- `pnpm lint`: Run ESLint to check for code issues.
- `pnpm format`: Format the code using Prettier.
- `pnpm expo install <package>`: Install Expo libraries with versions compatible with this SDK.

## Updating dependencies

This template pins dependency versions exactly and uses a hardened `pnpm-workspace.yaml`. Updates should be intentional, lockfile-backed, and checked by Expo before they are merged.

### 1. Use the pinned pnpm version

`pnpm-workspace.yaml` has `pmOnFail: error`, so `pnpm` commands fail if your local pnpm version does not match `package.json`.

```bash
pnpm --version
```

If it does not match the `packageManager` field, install the pinned version:

```bash
corepack enable
corepack prepare pnpm@11.1.2 --activate
```

If your environment uses a global pnpm binary instead of Corepack:

```bash
npm install -g pnpm@11.1.2
```

### 2. Update pnpm

Check the latest pnpm release:

```bash
npm view pnpm version
```

Then update the `packageManager` field in `package.json`, update your local pnpm to the same version, and verify:

```bash
pnpm --version
pnpm install --frozen-lockfile
```

### 3. Update Expo SDK packages

For patch updates within the current Expo SDK:

```bash
pnpm expo install --check
pnpm expo install --fix
pnpm dlx expo-doctor
```

For a full Expo SDK upgrade, upgrade one SDK at a time. Follow the [official Expo upgrade guide](https://docs.expo.dev/workflow/upgrading-expo-sdk-walkthrough/) and release notes, then run:

```bash
pnpm add expo@^55.0.0
pnpm expo install --fix
pnpm dlx expo-doctor
```

Replace `55` with the target SDK version. If you keep exact pins, convert any `^` or `~` versions written by Expo back to the exact versions selected in `pnpm-lock.yaml`, then run:

```bash
pnpm install --frozen-lockfile
```

If `minimumReleaseAge` blocks a brand-new Expo patch, prefer waiting until the package is at least 7 days old. Only add a temporary `minimumReleaseAgeExclude` entry when you intentionally need that new release immediately.

### 4. Update non-Expo packages

Check what is outdated:

```bash
pnpm outdated
```

Update ordinary JavaScript packages conservatively:

```bash
pnpm update --latest <package>
```

For React, React Native, Expo packages, Metro, NativeWind, Tailwind, Reanimated, Worklets, and Expo-managed native modules, prefer Expo's expected versions over npm's latest versions.

### 5. Verify the update

```bash
pnpm install --frozen-lockfile
pnpm expo install --check
pnpm dlx expo-doctor
pnpm typecheck
pnpm lint
```

For native dependency changes, rebuild the development app:

```bash
pnpm android
# or
pnpm ios
```

## pnpm troubleshooting

If any React Native library has autolinking, Metro resolution, or native build issues with `pnpm` isolated mode, temporarily test this change in `pnpm-workspace.yaml`:

```yaml
nodeLinker: hoisted
```

## Building an APK

1. Login to your Expo account:

   ```bash
   pnpm dlx eas-cli@latest login
   ```

2. Configure your project for EAS Build:

   ```bash
   pnpm dlx eas-cli@latest build:configure
   ```

3. Configure your project for future updates:

   ```bash
   pnpm dlx eas-cli@latest update:configure
   ```

4. Build the APK:

   ```bash
   pnpm dlx eas-cli@latest build --platform android --profile preview
   ```
