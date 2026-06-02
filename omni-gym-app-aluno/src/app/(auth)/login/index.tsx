import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { Text, View } from 'react-native';
import Animated, { LinearTransition } from 'react-native-reanimated';

import { alunoEmptyImg } from '@/assets/images';
import {
  Button,
  Checkbox,
  Image,
  Input,
  KeyboardAwareScrollView,
  Pressable,
} from '@/components/ui';
import { useAuth } from '@/contexts/useAuth';
import { useDimensions } from '@/hooks/common';
import { LoginForm, LoginSchema } from '@/validation/login.validation';

const Login = () => {
  const { insets, safeWidth } = useDimensions();
  const { login } = useAuth();

  const { control, handleSubmit } = useForm<LoginForm>({
    resolver: zodResolver(LoginSchema),
    defaultValues: __DEV__
      ? {
          identifier: 'a@a.com',
          password: 'a',
          requestRefresh: true,
        }
      : {
          identifier: '',
          password: '',
          requestRefresh: false,
        },
  });

  return (
    <KeyboardAwareScrollView
      contentContainerClassName="flex-grow"
      overScrollMode="never"
      style={{ marginTop: -insets.top }}
    >
      <Animated.View
        className="-mb-5 flex-grow justify-center gap-5 bg-primary-100 px-4 pb-10 pt-20"
        layout={LinearTransition}
        style={{ maxHeight: safeWidth }}
      >
        <Image
          contentFit="contain"
          source={alunoEmptyImg}
          style={{
            width: 150,
            height: 150,
            alignSelf: 'center',
            marginTop: -30,
          }}
        />

        <View className="gap-2">
          <Text className="font-montserrat_bold text-xl text-white">
            Bem-vindo Aluno!
          </Text>
        </View>
      </Animated.View>

      <Animated.View
        className="flex-grow gap-6 rounded-t-[20px] bg-white px-4 py-10"
        layout={LinearTransition}
      >
        <View className="gap-8">
          <View className="gap-2">
            <Text className="font-montserrat_extrabold text-xl text-primary-100">
              Faça login
            </Text>

            <Text className="font-montserrat_semibold text-sm text-neutral-60">
              Insira seus dados para continuar
            </Text>
          </View>

          <View className="gap-6">
            <Input
              control={control}
              keyboardType="email-address"
              label="E-mail"
              name="identifier"
              placeholder="Digite seu e-mail"
            />

            <View className="gap-4">
              <Input
                isPassword
                control={control}
                label="Senha"
                name="password"
                placeholder="Digite sua senha"
              />

              <Animated.View
                className="w-full flex-row items-center gap-2"
                layout={LinearTransition}
              >
                <Checkbox control={control} name="requestRefresh" />

                <Text className="font-inter text-base text-neutral-80">
                  Manter-me conectado
                </Text>
              </Animated.View>
            </View>
          </View>
        </View>

        <View className="gap-2">
          <Button text="Entrar" onPress={handleSubmit(login)} />

          <Pressable className="self-center rounded-full px-2 py-1">
            <Text className="font-montserrat_extrabold text-sm text-primary-100 underline">
              Esqueceu sua senha?
            </Text>
          </Pressable>
        </View>
      </Animated.View>
    </KeyboardAwareScrollView>
  );
};

export default Login;
