import { PropsWithChildren } from 'react';
import * as KeyboardController from 'react-native-keyboard-controller';
import { KeyboardAwareScrollViewProps } from 'react-native-keyboard-controller';

const KeyboardAwareScrollView = ({
  children,
  ...props
}: PropsWithChildren<KeyboardAwareScrollViewProps>) => {
  return (
    <KeyboardController.KeyboardAwareScrollView
      extraKeyboardSpace={-50}
      keyboardShouldPersistTaps="handled"
      showsVerticalScrollIndicator={false}
      {...props}
    >
      {children}
    </KeyboardController.KeyboardAwareScrollView>
  );
};

export default KeyboardAwareScrollView;
