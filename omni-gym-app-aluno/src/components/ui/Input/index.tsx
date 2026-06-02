import { useState } from 'react';
import {
  FieldValues,
  useController,
  UseControllerProps,
} from 'react-hook-form';
import {
  StyleProp,
  Text,
  TextInput,
  TextInputProps,
  TextStyle,
  View,
  ViewProps,
} from 'react-native';
import { MaskedTextInput, MaskedTextInputProps } from 'react-native-mask-text';
import Animated, {
  AnimatedProps,
  LinearTransition,
} from 'react-native-reanimated';

import { colors } from '@/global/colors';

import ErrorText from '../ErrorText';
import Icon, { IconProps } from '../Icon';
import Pressable from '../Pressable';

type MaskedInputOptions = NonNullable<MaskedTextInputProps['options']> & {
  dateFormat?: string;
  timeFormat?: string;
};

type Props<TFieldValues extends FieldValues> = {
  label?: string;
  isPassword?: boolean;
  placeholder?: string;
  mask?: MaskedTextInputProps['mask'];
  type?: MaskedTextInputProps['type'];
  options?: MaskedInputOptions;
  minHeight?: number;
  containerProps?: AnimatedProps<ViewProps>;
  icon?: IconProps;
  suffix?: string;
} & TextInputProps &
  UseControllerProps<TFieldValues>;

const Input = <TFieldValues extends FieldValues>({
  label,
  isPassword,
  placeholder,
  mask,
  type,
  options,
  control,
  name,
  autoCapitalize = 'none',
  minHeight,
  containerProps,
  icon,
  suffix,
  multiline,
  maxLength,
  onChangeText,
  ...props
}: Props<TFieldValues>) => {
  const [passwordHidden, setPasswordHidden] = useState(isPassword);

  const length = () => {
    if (maxLength) {
      return maxLength;
    }
    if (multiline || minHeight) {
      return 250;
    }
    return 100;
  };

  const {
    field,
    fieldState: { error },
  } = useController({
    control,
    name,
  });

  const value = field.value == null ? '' : String(field.value);

  const maskPattern =
    mask ||
    (type === 'date'
      ? (options?.dateFormat ?? 'yyyy/mm/dd').replace(/[a-zA-Z]/g, '9')
      : undefined) ||
    (type === 'time' ? (options?.timeFormat ?? 'HH:mm:ss') : undefined);

  const handleChangeText = (text: string) => {
    field.onChange(text);
    onChangeText?.(text);
  };

  const inputStyle: StyleProp<TextStyle> = {
    flexGrow: 1,
    height: '100%',
    padding: 12,
    fontSize: 14,
    color: colors.neutral[60],
    paddingRight: isPassword || icon ? 44 : undefined,
  };

  const commonProps: Omit<TextInputProps, 'onChangeText'> = {
    autoCapitalize,
    maxLength: length(),
    multiline: !!minHeight || multiline,
    placeholder,
    placeholderTextColor: colors.neutral[40],
    secureTextEntry: passwordHidden,
    style: inputStyle,
    textAlignVertical: 'top',
    value,
    onBlur: field.onBlur,
    ...props,
  };

  return (
    <Animated.View
      className="w-full gap-2"
      layout={LinearTransition}
      {...containerProps}
    >
      {label && (
        <Text className="font-inter_semibold text-base text-neutral-60">
          {label}
        </Text>
      )}

      <View className="w-full gap-px">
        <View
          className="w-full flex-row items-center rounded-lg border border-neutral-20 bg-white"
          style={{ minHeight }}
        >
          {maskPattern || type ? (
            <MaskedTextInput
              ref={field.ref}
              mask={maskPattern}
              options={options}
              type={type}
              {...commonProps}
              onChangeText={handleChangeText}
            />
          ) : (
            <TextInput
              ref={field.ref}
              {...commonProps}
              onChangeText={handleChangeText}
            />
          )}

          {isPassword && (
            <Pressable
              className="absolute right-2 items-center justify-center overflow-hidden rounded-full p-1"
              onPress={() => {
                setPasswordHidden(!passwordHidden);
              }}
            >
              <Icon
                key={passwordHidden ? 'EyeOff' : 'Eye'}
                name={passwordHidden ? 'EyeOffIcon' : 'EyeIcon'}
                size={24}
                strokeWidth={1.5}
              />
            </Pressable>
          )}

          {icon && (
            <View className="absolute right-2 self-center">
              <Icon {...icon} />
            </View>
          )}

          {suffix && (
            <Text className="absolute right-2 self-center text-base text-neutral-600">
              {suffix}
            </Text>
          )}
        </View>

        <ErrorText text={error?.message} />
      </View>
    </Animated.View>
  );
};

export default Input;
