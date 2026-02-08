import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Field, FieldGroup, FieldLabel } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { useUsers } from "@/context/context";
import { useState } from "react";
import { useNavigate } from "react-router";
import { z } from "zod";


const createUserFormSchema = z.object({
    name: z.string().min(2, "Name is required"),
    lastName: z.string().min(2, "Last name is required"),
    email: z.string().email("Invalid email"),
    street: z.string().min(3, "Street is required"),
    city: z.string().min(2, "City is required"),
    country: z.string().min(2, "Country is required"),
    isHost: z.boolean()
})

export type CreateUserFormData = z.infer<typeof createUserFormSchema>;

export function CreateUser() {
    const [isHost, setIsHost] = useState(false);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const { createUser, loading } = useUsers();
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.currentTarget;
        const formData = new FormData(form);

        const data = {
            name: formData.get("name") as string,
            lastName: formData.get("lastName") as string,
            email: formData.get("email") as string,
            street: formData.get("street") as string,
            city: formData.get("city") as string,
            country: formData.get("country") as string,
            isHost
        };

        const parsed = createUserFormSchema.safeParse(data);

        if (!parsed.success) {
            const fieldErrors: Record<string, string> = {};
            parsed.error.issues.forEach(issue => {
                fieldErrors[issue.path[0] as string] = issue.message;
            });
            setErrors(fieldErrors);
            return;
        }

        setErrors({});
        const fullAddress = `${parsed.data.street}, ${parsed.data.city}, ${parsed.data.country}`;

        const payload = {
            user: {
                name: parsed.data.name,
                lastName: parsed.data.lastName,
                email: parsed.data.email,
                address: fullAddress
            },
            host: parsed.data.isHost
        };

        await createUser(payload);
        form.reset();
        setIsHost(false);
        navigate("/");
    }

    return (
        <div className=" w-full flex flex-col h-screen justify-center items-center">
            <Card className="w-full max-w-4xl">
                <CardHeader>
                    <CardTitle className="text-xl">Create a new User</CardTitle>
                    <CardDescription>
                        Enter your information below to create your account
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit}>
                        <FieldGroup >
                            <div className="grid grid-cols-2 gap-4">
                                <Field className="gap-1">
                                    <FieldLabel htmlFor="name">Name</FieldLabel>
                                    <Input id="name" name="name" placeholder="John" aria-invalid={!!errors.name} />
                                    {errors.name && <p className="text-red-500 text-sm">{errors.name}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="lastName">Last Name</FieldLabel>
                                    <Input id="lastName" name="lastName" placeholder="Doe" aria-invalid={!!errors.lastName} />
                                    {errors.lastName && <p className="text-red-500 text-sm">{errors.lastName}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="email">Email</FieldLabel>
                                    <Input id="email" name="email" type="email" placeholder="m@example.com" aria-invalid={!!errors.email} />
                                    {errors.email && <p className="text-red-500 text-sm">{errors.email}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="street">Street</FieldLabel>
                                    <Input id="street" name="street" placeholder="Via Roma, 56" aria-invalid={!!errors.street} />
                                    {errors.street && <p className="text-red-500 text-sm">{errors.street}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="city">City</FieldLabel>
                                    <Input id="city" name="city" placeholder="Firenze" aria-invalid={!!errors.city} />
                                    {errors.city && <p className="text-red-500 text-sm">{errors.city}</p>}
                                </Field>

                                <Field className="gap-1">
                                    <FieldLabel htmlFor="country">Country</FieldLabel>
                                    <Input id="country" name="country" placeholder="Italy" aria-invalid={!!errors.country} />
                                    {errors.country && <p className="text-red-500 text-sm">{errors.country}</p>}
                                </Field>
                            </div>

                            <Field className="gap-1">
                                <FieldLabel htmlFor="isHost">User type</FieldLabel>
                                <div className="flex items-center gap-2">
                                    <input
                                        id="isHost"
                                        type="checkbox"
                                        checked={isHost}
                                        onChange={(e) => setIsHost(e.target.checked)}
                                        className="h-4 w-4"
                                    />
                                    <label htmlFor="isHost" className="text-sm underline">
                                        This user is also a Host
                                    </label>
                                </div>
                            </Field>

                            <Button type="submit" disabled={loading}>
                                {loading ? "Creating..." : "Create Account"}
                            </Button>
                        </FieldGroup>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}